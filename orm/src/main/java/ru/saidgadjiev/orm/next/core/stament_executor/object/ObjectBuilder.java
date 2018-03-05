package ru.saidgadjiev.orm.next.core.stament_executor.object;

import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.field_type.*;
import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

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

    public ObjectBuilder<T> buildForeign(DatabaseResults data, List<ForeignFieldType> resultFieldTypes, Set<Class<?>> parents) throws Exception {
        parents.add(tableInfo.getTableClass());

        for (ForeignFieldType fieldType : resultFieldTypes) {
            if (!parents.contains(fieldType.getForeignFieldClass())) {
                TableInfo<?> foreignTableInfo = TableInfo.build(fieldType.getForeignFieldClass());
                Session<Object, Object> foreignDao = new BaseSessionManagerImpl(dataSource).forClass(foreignTableInfo.getTableClass());
                Select select = Select.buildQueryById(foreignTableInfo.getTableName(), foreignTableInfo.getPrimaryKey().get(), data.getObject(fieldType.getColumnName()));
                DefaultVisitor visitor = new DefaultVisitor(dataSource.getDatabaseType());

                select.accept(visitor);
                parents.add(tableInfo.getTableClass());
                Object foreignObject = foreignDao.query(visitor.getQuery(), results -> new ObjectBuilder<>(dataSource, foreignTableInfo)
                        .newObject()
                        .buildBase(results, tableInfo.toDBFieldTypes())
                        .buildForeign(results, tableInfo.toForeignFieldTypes(), parents)
                        .buildForeignCollection(tableInfo.toForeignCollectionFieldTypes())
                        .build())
                        .getFirstResult();

                fieldType.assign(object, foreignObject);
            }
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
                ForeignFieldType foreignField = (ForeignFieldType) new ForeignCollectionFieldTypeFactory().createFieldType(fieldType.getForeignField());

                GenericResults<?> genericResults = foreignDao.query(
                        "SELECT * FROM '" + foreignTableInfo.getTableName() + "' WHERE '" + foreignField.getColumnName() + "' = " + idField.access(object),
                        results -> new ObjectBuilder<>(dataSource, foreignTableInfo)
                                .newObject()
                                .buildBase(results, foreignTableInfo.toDBFieldTypes())
                                .buildForeign(results, foreignTableInfo.toForeignFieldTypes(), null)
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
