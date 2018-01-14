package ru.said.miami.orm.core.stamentExecutor.object;

import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.field.fieldTypes.ForeignCollectionFieldType;
import ru.said.miami.orm.core.field.fieldTypes.ForeignFieldType;
import ru.said.miami.orm.core.stamentExecutor.DatabaseResults;
import ru.said.miami.orm.core.queryBuilder.PreparedQuery;
import ru.said.miami.orm.core.queryBuilder.QueryBuilder;
import ru.said.miami.orm.core.table.TableInfo;

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
        } else {
            for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
                fieldType.assign(object, data.getObject(fieldType.getColumnName()));
            }
        }


        return this;
    }

    public ObjectBuilder<T> buildForeign(DatabaseResults data) throws Exception {
        for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
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

    public ObjectBuilder<T> buildForeignCollection() throws Exception {
        for (ForeignCollectionFieldType fieldType : tableInfo.toForeignCollectionFieldTypes()) {
            TableInfo<?> foreignTableInfo = TableInfo.TableInfoCache.build(fieldType.getForeignFieldClass());
            Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);
            QueryBuilder<Object> queryBuilder = foreignDao.queryBuilder();

            if (tableInfo.getPrimaryKeys().isPresent() && foreignTableInfo.getPrimaryKeys().isPresent()) {
                DBFieldType idField = tableInfo.getPrimaryKeys().get();
                ForeignFieldType foreignField = ForeignFieldType.ForeignFieldTypeCache.build(fieldType.getForeignField());
                PreparedQuery query = queryBuilder
                        .where(queryBuilder.whereBuilder()
                                .eq(foreignField.getColumnName(), idField.access(object))
                                .build())
                        .prepare();

                List<Object> foreignObjects = foreignDao.query(query);

                for (Object foreignObject : foreignObjects) {
                    foreignField.assign(foreignObject, object);
                }

                fieldType.addAll(object, foreignObjects);
            }
        }

        return this;
    }

    public T build() {
        return object;
    }
}
