package ru.said.orm.next.core.stament_executor.object;

import ru.said.orm.next.core.dao.BaseDaoImpl;
import ru.said.orm.next.core.dao.Dao;
import ru.said.orm.next.core.dao.DaoManager;
import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.ForeignCollectionFieldType;
import ru.said.orm.next.core.field.field_type.ForeignFieldType;
import ru.said.orm.next.core.stament_executor.DatabaseResults;
import ru.said.orm.next.core.stament_executor.GenericResults;
import ru.said.orm.next.core.table.TableInfo;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.List;

public class ObjectBuilder<T> {

    private TableInfo<T> tableInfo;

    private DataSource dataSource;

    private T object;

    public ObjectBuilder(DataSource dataSource, TableInfo<T> tableInfo) {
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
                fieldType.assign(object, data.getObject(fieldType.getColumnName()));
            }
        }

        return this;
    }

    public ObjectBuilder<T> buildForeign(DatabaseResults data, List<ForeignFieldType> resultFieldTypes) throws Exception {
        for (ForeignFieldType fieldType : resultFieldTypes) {
            TableInfo<?> foreignTableInfo = TableInfo.TableInfoCache.build(fieldType.getForeignFieldClass());
            Object val = newObject(foreignTableInfo.getConstructor());

            if (foreignTableInfo.getPrimaryKeys().isPresent()) {
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
            TableInfo<?> foreignTableInfo = TableInfo.TableInfoCache.build(fieldType.getForeignFieldClass());
            Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);

            if (tableInfo.getPrimaryKeys().isPresent() && foreignTableInfo.getPrimaryKeys().isPresent()) {
                DBFieldType idField = tableInfo.getPrimaryKeys().get();
                ForeignFieldType foreignField = ForeignFieldType.ForeignFieldTypeCache.build(fieldType.getForeignField());

                GenericResults<?> genericResults = foreignDao.query(
                        "SELECT * FROM '" + foreignTableInfo.getTableName() + "' WHERE '" + foreignField.getColumnName() + "' = " + idField.access(object),
                        results -> new ObjectBuilder<>(dataSource, foreignTableInfo)
                                .newObject()
                                .buildBase(results, foreignTableInfo.toDBFieldTypes())
                                .buildForeign(results, foreignTableInfo.toForeignFieldTypes())
                                .buildForeignCollection(foreignTableInfo.toForeignCollectionFieldTypes())
                );
                List<?> objects = genericResults.getResults();

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
