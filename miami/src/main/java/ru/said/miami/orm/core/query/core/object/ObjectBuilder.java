package ru.said.miami.orm.core.query.core.object;

import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.DBFieldTypeCache;
import ru.said.miami.orm.core.field.ForeignCollectionFieldType;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.query.core.Query;
import ru.said.miami.orm.core.query.core.query_builder.QueryBuilder;
import ru.said.miami.orm.core.table.TableInfo;
import ru.said.miami.orm.core.table.TableInfoCache;

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

    public ObjectBuilder<T> newObject() throws Exception  {
        this.object = (T) newObject(tableInfo.getConstructor());

        return this;
    }

    public ObjectBuilder<T> buildBase(IMiamiData data) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (!fieldType.isForeign()) {
                fieldType.assign(object, data.getObject(fieldType.getColumnName()));
            }
        }

        return this;
    }

    public ObjectBuilder<T> buildForeign(IMiamiData data) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isForeign()) {
                TableInfo<?> foreignTableInfo = TableInfoCache.build(fieldType.getForeignFieldType());
                Object val = newObject(foreignTableInfo.getConstructor());

                if (foreignTableInfo.getIdField().isPresent()) {
                    foreignTableInfo.getIdField().get().assign(val, data.getObject(fieldType.getColumnName()));
                }
                fieldType.assign(object, val);
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

    public ObjectBuilder<T> buildForeignCollection() throws Exception {
        for (ForeignCollectionFieldType fieldType : tableInfo.toForeignCollectionFieldTypes()) {
            TableInfo<?> foreignTableInfo = TableInfoCache.build(fieldType.getForeignFieldClass());
            Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);
            QueryBuilder<Object> queryBuilder = foreignDao.queryBuilder();

            if (tableInfo.getIdField().isPresent() && foreignTableInfo.getIdField().isPresent()) {
                DBFieldType idField = tableInfo.getIdField().get();
                DBFieldType foreignField = DBFieldTypeCache.build(fieldType.getForeignField());
                Query<List<Object>> query = queryBuilder.where()
                        .eq(foreignField.getColumnName(), String.valueOf(idField.access(object)))
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
