package ru.saidgadjiev.orm.next.core.stament_executor.object;

import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.field_type.*;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.utils.TableInfoUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

public class ObjectBuilder<T> {

    private TableInfo<T> tableInfo;

    private ConnectionSource dataSource;

    private T object;

    public ObjectBuilder(ConnectionSource dataSource, TableInfo<T> tableInfo) {
        this.tableInfo = tableInfo;
        this.dataSource = dataSource;
    }

    public ObjectBuilder<T> newObject() throws Exception {
        this.object = (T) newObject(tableInfo.getConstructor());

        return this;
    }

    public ObjectBuilder<T> buildBase(DatabaseResults data, List<DBFieldType> resultFieldTypes) throws Exception {
        if (resultFieldTypes != null) {
            for (DBFieldType fieldType : resultFieldTypes) {
                Object value = fieldType.getDataPersister().parseSqlToJava(fieldType, data.getObject(fieldType.getColumnName()));

                fieldType.assign(object, value);
            }
        }

        return this;
    }

    public ObjectBuilder<T> buildForeign(DatabaseResults data, List<ForeignFieldType> resultFieldTypes) throws Exception {
        for (ForeignFieldType fieldType : resultFieldTypes) {
            Constructor<?> foreignClassConstructor = TableInfoUtils.lookupDefaultConstructor(fieldType.getForeignFieldClass())
                    .orElseThrow(() -> new IllegalArgumentException("Class " + fieldType.getForeignFieldClass() + " doesn't have default constructor"));

            Object val = newObject(foreignClassConstructor);
            Optional<IDBFieldType> primaryKey = TableInfoUtils.resolvePrimaryKey(fieldType.getForeignFieldClass());

            if (primaryKey.isPresent()) {
                fieldType.getForeignPrimaryKey().assign(val, data.getObject(fieldType.getColumnName()));
            }
            fieldType.assign(object, val);
        }

        return this;
    }

    private Object newObject(Constructor<?> constructor) throws Exception {
        Object object;

        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
            object = constructor.newInstance();
            constructor.setAccessible(false);
        } else {
            object = constructor.newInstance();
        }

        return object;
    }

    public ObjectBuilder<T> buildForeignCollection(List<ForeignCollectionFieldType> resultFieldTypes) throws Exception {
        for (ForeignCollectionFieldType fieldType : resultFieldTypes) {
            TableInfo<?> foreignTableInfo = TableInfo.build(fieldType.getForeignFieldClass());
            Session<Object, ?> foreignDao = new BaseSessionManagerImpl(dataSource).forClass(foreignTableInfo.getTableClass());

            if (tableInfo.getPrimaryKey().isPresent() && foreignTableInfo.getPrimaryKey().isPresent()) {
                IDBFieldType idField = tableInfo.getPrimaryKey().get();
                ForeignFieldType foreignField = (ForeignFieldType) new ForeignFieldTypeFactory().createFieldType(fieldType.getForeignField());
                SelectStatement<Object> selectStatement = foreignDao.selectQuery();

                selectStatement.setWhere(new Criteria().add(Restrictions.eq(foreignField.getFieldName(), idField.access(object))));

                List<?> objects = foreignDao.query(selectStatement);

                for (Object foreignObject : objects) {
                    foreignField.assign(foreignObject, object);
                }
                fieldType.addAll(object, objects);
            }
        }

        return this;
    }

    public T build() {
        return object;
    }
}
