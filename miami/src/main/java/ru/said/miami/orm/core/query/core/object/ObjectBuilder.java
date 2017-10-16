package ru.said.miami.orm.core.query.core.object;

import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.ForeignCollectionFieldType;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.query.core.query_builder.QueryBuilder;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class ObjectBuilder<T> {

    private TableInfo<T> tableInfo;

    private DataSource dataSource;

    private T object;

    public ObjectBuilder(DataSource dataSource, TableInfo<T> tableInfo) {
        this.tableInfo = tableInfo;
        this.dataSource = dataSource;
    }

    public ObjectBuilder<T> newObject() throws IllegalAccessException, InvocationTargetException, InstantiationException  {
        this.object = tableInfo.getConstructor().newInstance();

        return this;
    }

    public ObjectBuilder<T> buildBase(IMiamiData data) throws SQLException, IllegalAccessException {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (!fieldType.isForeign()) {
                fieldType.assignField(object, data.getObject(fieldType.getFieldName()));
            }
        }

        return this;
    }

    public ObjectBuilder<T> buildForeign(IMiamiData data) throws NoSuchMethodException, NoSuchFieldException, SQLException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isForeign()) {
                TableInfo<?> foreignTableInfo = TableInfo.buildTableInfo(fieldType.getForeignFieldType());
                Object val = foreignTableInfo.getConstructor().newInstance();

                if (foreignTableInfo.getIdField().isPresent()) {
                    foreignTableInfo.getIdField().get().assignField(val, data.getObject(fieldType.getFieldName()));
                }
                fieldType.assignField(object, val);
            }
        }

        return this;
    }

    public ObjectBuilder<T> buildForeignCollection(IMiamiData data) throws NoSuchMethodException, NoSuchFieldException, SQLException, IllegalAccessException {
        for (ForeignCollectionFieldType fieldType : tableInfo.toForeignCollectionFieldTypes()) {
            TableInfo<?> foreignTableInfo = TableInfo.buildTableInfo(fieldType.getForeignFieldClass());
            Dao<?, ?> foreignDao = DaoManager.createDAOWithTableInfo(dataSource, foreignTableInfo);
            QueryBuilder<?> queryBuilder = foreignDao.queryBuilder();

            if (tableInfo.getIdField().isPresent() && foreignTableInfo.getIdField().isPresent()) {
                DBFieldType idField = tableInfo.getIdField().get();
                DBFieldType foreignField = DBFieldType.buildFieldType(fieldType.getForeignField());

                queryBuilder.where().eq(foreignField.getFieldName(), String.valueOf(idField.getValue(object)));
                List<?> foreignObjects = queryBuilder.execute();

                for (Object foreignObject : foreignObjects) {
                    foreignField.assignField(foreignObject, object);
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
