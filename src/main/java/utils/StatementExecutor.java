package utils;

import clause.Update;
import clause.Where;
import clause.element.LongLiteral;
import clause.element.StringLiteral;
import clause.query.InsertQuery;
import clause.query.UpdateValue;
import dao.builder.ObjectBuilder;
import dao.cache.Cache;
import dao.cache.CacheResultSet;
import dao.visitor.QueryVisitorImpl;
import db.dialect.IDialect;
import field.*;
import org.apache.log4j.Logger;
import table.TableInfo;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by said on 11.03.17.
 */
public class StatementExecutor {

    private final static Logger LOGGER = Logger.getLogger(StatementExecutor.class);
    private Cache cache = new Cache();
    private DataSource dataSource;
    private IDialect dialect;

    public StatementExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Object queryForId(TableInfo tableInfo, int id) throws SQLException {
        Statement statement = null;
        Connection connection = null;

        try {
            String query = "SELECT * FROM " + tableInfo.getTableName() + " WHERE " + ((TableField) tableInfo.getId().getAnnotation(TableField.class)).fieldName() + "=" + id;
            ResultSet resultSet = null;
            CacheResultSet cacheResultSet;

            try {
                ObjectBuilder objectBuilder = new ObjectBuilder(tableInfo.getTable());

                if (cache.has(query)) {
                    cacheResultSet = cache.get(query);
                } else {
                    connection = dataSource.getConnection();
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);
                    cacheResultSet = new CacheResultSet(resultSet);
                    cache.add(query, cacheResultSet);
                }
                List<FieldWrapper> fields = tableInfo.getFields();

                while (cacheResultSet.next()) {
                    for (FieldWrapper field : fields) {
                        if (field.isAnnotationPresent(TableField.class)) {
                            if (field.isAnnotationPresent(OneToOne.class)) {
                                TableInfo tableInfo1 = new TableInfo(field.getType());
                                Object tmp = queryForId(tableInfo1, (Integer) cacheResultSet.getObject(((TableField) field.getAnnotation(TableField.class)).fieldName()));

                                objectBuilder.buildOneToOne(field, tmp);
                            } else if (field.isAnnotationPresent(OneToMany.class)) {
                                Long mainId = resultSet.getLong(((TableField) tableInfo.getId().getAnnotation(TableField.class)).fieldName());
                                TableInfo tableInfo1 = new TableInfo(ReflectionUtils.getCollectionGenericClass(field.getField()));
                                FieldWrapper foreignField = tableInfo1.getFieldByMappedByNameInChild(((OneToMany) field.getAnnotation(OneToMany.class)).mappedBy());
                                String sql = "SELECT " + ((TableField) tableInfo1.getId().getAnnotation(TableField.class)).fieldName()
                                        + " as id FROM " + tableInfo1.getTableName() + " WHERE "
                                        + ((TableField) foreignField.getAnnotation(TableField.class)).fieldName() + "=" + mainId;
                                List<Object> objects = new ArrayList<>();
                                Collection<Integer> ids = MiamiUtils.getIntegerCollection(connection, sql, "id", new HashSet<>());

                                for (Integer foreignId : ids) {
                                    objects.add(queryForId(tableInfo1, foreignId));
                                }

                                objectBuilder.buildOneToMany(field, objects);
                            } else {
                                objectBuilder.buildField(field, cacheResultSet.getObject(((TableField) field.getAnnotation(TableField.class)).fieldName()));
                            }
                        }
                    }
                }
                return objectBuilder.build();
            } catch (Exception ex) {
                throw new SQLException(ex);
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<Object> queryForAll(TableInfo tableInfo) throws SQLException {
        /*Statement statement = null;
        Connection connection = dataSource.getConnection();

        try {
            statement = connection.createStatement();
            String queryForAll = "SELECT " + tableInfo.getId().getAnnotation(TableField.class).fieldName() + " FROM " + tableInfo.getTableName();
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery(queryForAll);
                List<Field> fields = tableInfo.getFields();
                fields.add(tableInfo.getId());
                List<Object> result = new ArrayList<>();

                while (resultSet.next()) {
                    Object tmp = queryForId(tableInfo, (Integer) resultSet.getObject(tableInfo.getId().getAnnotation(TableField.class).fieldName()));

                    result.add(tmp);
                }

                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            return null;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }*/

        return null;
    }

    public List<Object> queryForAll(TableInfo tableInfo, String sql) throws SQLException {
       /* Statement statement = null;
        Connection connection = dataSource.getConnection();

        try {
            statement = connection.createStatement();
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery(sql);
                List<Object> result = new ArrayList<>();

                while (resultSet.next()) {
                    Object tmp = queryForId(tableInfo, (Integer) resultSet.getObject(tableInfo.getId().getAnnotation(TableField.class).fieldName()));

                    result.add(tmp);
                }

                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            return null;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }*/

        return null;
    }

    public List<Object> queryForWhere(TableInfo tableInfo, Where where) throws SQLException {
        /*PreparedQuery query = new PreparedQuery();

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT " + tableInfo.getId().getAnnotation(TableField.class).fieldName() + " FROM " + tableInfo.getTableName() + where.getStringQuery())) {
                    List<Field> fields = tableInfo.getFields();
                    fields.add(tableInfo.getId());
                    List<Object> result = new ArrayList<>();

                    while (resultSet.next()) {
                        Object tmp = queryForId(tableInfo, (Integer) resultSet.getObject(tableInfo.getId().getAnnotation(TableField.class).fieldName()));

                        result.add(tmp);
                    }

                    return result;
                }
            }
        }*/

        return null;
    }

    public int queryForUpdate(TableInfo tableInfo, Update update) throws SQLException {
        /*Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("UPDATE ").append(tableInfo.getTableName()).append(update.getStringQuery());

            return statement.executeUpdate(sb.toString());
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }*/

        return -1;
    }

    public boolean deleteForWhere(TableInfo tableInfo, Where where) throws SQLException {
        /*Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("DELETE FROM ").append(tableInfo.getTableName()).append(" ").append(where.toString());

            return statement.execute(sb.toString());
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }*/

        return false;
    }

    public void fillManyToMany(TableInfo tableInfo, TableInfo tableInfo1, FieldWrapper field, Object result) throws SQLException {
        try {
            String manyToManyTableName = TableUtils.getManyToManyRelationTableName(dataSource.getConnection(), tableInfo.getTableName(), tableInfo1.getTableName());
            String fieldId1 = manyToManyTableName.substring(0, manyToManyTableName.indexOf("_")) + "_id";
            String fieldId2 = manyToManyTableName.substring(manyToManyTableName.indexOf("_") + 1) + "_id";

            if (manyToManyTableName.endsWith(tableInfo.getTableName())) {
                String tmp = fieldId1;

                fieldId1 = fieldId2;
                fieldId2 = tmp;
            }
            String sql = "SELECT " + fieldId2 + " FROM " + manyToManyTableName + " WHERE " + fieldId1 + "=" + tableInfo.getId().getValue(result);

            Collection<Integer> ids = MiamiUtils.getIntegerCollection(dataSource.getConnection(), sql, fieldId2, new HashSet<>());

            for (Integer id: ids) {
                Object tmp = queryForId(tableInfo1, id);
                FieldWrapper manyToManyField = tableInfo1.getFieldByNameInManyToManyRelation(((ManyToMany) field.getAnnotation(ManyToMany.class)).mappedBy());
                ((List) tableInfo.getMethodByName(MethodNameUtils.makeGetterMethodName(manyToManyField)).invoke(tmp)).add(result);
                ((List) tableInfo.getMethodByName(MethodNameUtils.makeGetterMethodName(field)).invoke(result)).add(tmp);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public void create(Object object) throws SQLException {
        TableInfo tableInfo = new TableInfo(object.getClass());
        InsertQuery insertQuery = new InsertQuery(tableInfo.getTableName());

        try {
            for (FieldWrapper wrapper : tableInfo.getFields()) {
                if (wrapper.isAnnotationPresent(TableField.class)) {
                    if (wrapper.isAnnotationPresent(OneToOne.class) || wrapper.isAnnotationPresent(ManyToOne.class)) {
                        Object foreignObject = tableInfo.getMethodByName(MethodNameUtils.makeGetterMethodName(wrapper)).invoke(object);
                        insertQuery.addUpdateValue(new UpdateValue(wrapper.getName(), new LongLiteral((Long) new TableInfo(foreignObject.getClass()).getId().getValue(foreignObject))));
                    }
                }
                insertQuery.addUpdateValue(new UpdateValue(wrapper.getName(), new StringLiteral((String) wrapper.getValue(object))));
            }
            QueryVisitorImpl visitor = new QueryVisitorImpl();

            insertQuery.accept(visitor);
            MiamiUtils.execute(dataSource.getConnection(), visitor.preparedQuery());

            FieldWrapper idField = tableInfo.getId();
            int lastInsertRowId = getLastInsertRowId();
            tableInfo.getMethodByName(MethodNameUtils.makeSetterMethodName(idField)).invoke(object, lastInsertRowId);

            //Для сетинга зависимого объекта при связи ManyToOne
            for (FieldWrapper fieldWrapper : tableInfo.getManyToOneRelations()) {
                Object foreignObject = tableInfo.getMethodByName(MethodNameUtils.makeGetterMethodName(fieldWrapper)).invoke(object);
                TableInfo tableInfo1 = new TableInfo(foreignObject.getClass());
                FieldWrapper mappedBy = tableInfo1.getFieldByMappedNameInOneToManyRelation(fieldWrapper.getName());

                ((List) tableInfo1.getMethodByName(MethodNameUtils.makeGetterMethodName(mappedBy)).invoke(foreignObject)).add(object);
            }
            for (FieldWrapper fieldWrapper : tableInfo.getManyToManyRelations()) {
                TableInfo tableInfo1 = new TableInfo(ReflectionUtils.getCollectionGenericClass(fieldWrapper.getField()));
                TableUtils.createManyToManyTable(dataSource.getConnection(), tableInfo, tableInfo1);

                List<Object> objects = (List) tableInfo.getMethodByName(MethodNameUtils.makeGetterMethodName(fieldWrapper)).invoke(object);
                Long id = (Long) tableInfo.getId().getValue(object);

                for (Object relationObject : objects) {
                    Long idForeign = (Long) tableInfo1.getId().getValue(relationObject);

                    insertManyToManyRelation(id, idForeign, tableInfo, tableInfo1);
                    FieldWrapper manyToManyField = tableInfo1.getFieldByNameInManyToManyRelation(((ManyToMany) fieldWrapper.getAnnotation(ManyToMany.class)).mappedBy());

                    ((List) tableInfo1.getMethodByName(MethodNameUtils.makeGetterMethodName(manyToManyField)).invoke(relationObject)).add(object);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    private Integer getLastInsertRowId() throws SQLException {
        return MiamiUtils.getInt(dataSource.getConnection(), "SELECT " + dialect.lastInsertId() + " AS last_id", "last_id");
    }

    private void insertManyToManyRelation(long id1, long id2, TableInfo tableInfo, TableInfo tableInfo1) throws SQLException {
        String manyToManyTableName = TableUtils.getManyToManyRelationTableName(dataSource.getConnection(), tableInfo.getTableName(), tableInfo1.getTableName());
        String fieldId1 = manyToManyTableName.substring(0, manyToManyTableName.indexOf("_")) + "_id";
        String fieldId2 = manyToManyTableName.substring(manyToManyTableName.indexOf("_") + 1) + "_id";

        if (manyToManyTableName.endsWith(tableInfo.getTableName())) {
            long tmp = id1;

            id1 = id2;
            id2 = tmp;
        }
        if (isExistRelation(id1, id2, manyToManyTableName)) {
            return;
        }

        String sql = "INSERT INTO " + manyToManyTableName + "(" + fieldId1 + "," + fieldId2 + ") VALUES(" + id1 + ", " + id2 + ")";
        MiamiUtils.execute(dataSource.getConnection(), sql);
    }

    private boolean isExistRelation(long id1, long id2, String tableName) throws SQLException {
        String fieldId1 = tableName.substring(0, tableName.indexOf("_")) + "_id";
        String fieldId2 = tableName.substring(tableName.indexOf("_") + 1) + "_id";
        String sql = "SELECT * from " + tableName + " WHERE " + fieldId1 + "=" + id1 + " AND " + fieldId2 + "=" + id2;

        return MiamiUtils.getStringCollection(dataSource.getConnection(), sql, fieldId1, new HashSet<>()).isEmpty();
    }
}
