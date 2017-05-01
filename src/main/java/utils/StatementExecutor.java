package utils;

import field.DBField;
import field.ManyToMany;
import field.OneToMany;
import org.apache.log4j.Logger;
import support.JDBCConnectionSource;
import table.TableInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 11.03.17.
 */
public class StatementExecutor {

    private final static Logger LOGGER = Logger.getLogger(StatementExecutor.class);


    private JDBCConnectionSource connectionSource;

    public StatementExecutor(JDBCConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    public static void execute(JDBCConnectionSource connectionSource, String sql) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            statement.execute(sql);
            LOGGER.debug(sql);
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }
    }

    public Object queryForId(TableInfo<?> tableInfo, long id) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            String query = "SELECT * FROM " + tableInfo.getTableName() + " WHERE " + tableInfo.getId().getAnnotation(DBField.class).fieldName() + "=" + id;
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery(query);
                List<Field> fields = tableInfo.getFields();
                fields.add(tableInfo.getId());
                Object result = tableInfo.getTable().newInstance();

                while (resultSet.next()) {
                    for (Field field : fields) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, resultSet.getObject(field.getAnnotation(DBField.class).fieldName()));
                    }
                    for (Field field : tableInfo.getOneToOneRelations()) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        TableInfo<?> tableInfo1 = new TableInfo<>(field.getType());
                        Object tmp = queryForId(tableInfo1, resultSet.getLong(tableInfo.getId().getAnnotation(DBField.class).fieldName()));

                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, tmp);
                    }
                    for (Field field : tableInfo.getOneToManyRelations()) {
                        Field idField = tableInfo.getId();
                        String getIdMethod = "get" + idField.getName().substring(0, 1).toUpperCase() + idField.getName().substring(1);
                        Long mainId = (Long) ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), getIdMethod, null).invoke(result);
                        TableInfo<?> tableInfo1 = new TableInfo<>(ReflectionUtils.getCollectionGenericClass(field));
                        Field foreignField = tableInfo1.getFieldByMappedByNameInChild(field.getAnnotation(OneToMany.class).mappedBy());
                        String sql = "SELECT " + tableInfo1.getId().getName()
                                + " FROM " + tableInfo1.getTableName() + " WHERE "
                                + foreignField.getAnnotation(DBField.class).fieldName() + "=" + mainId;
                        ResultSet resultSet2 = null;
                        List<Object> objects = new ArrayList<>();
                        try {
                            resultSet2 = statement.executeQuery(sql);

                            while (resultSet2.next()) {
                                Object tmp = queryForId(tableInfo1, resultSet2.getLong(tableInfo1.getId().getAnnotation(DBField.class).fieldName()));
                                Field childMappedByField = tableInfo1.getFieldByMappedByNameInChild(field.getAnnotation(OneToMany.class).mappedBy());
                                String setParentMethodName = "set" + childMappedByField.getName().substring(0, 1).toUpperCase() + childMappedByField.getName().substring(1);

                                ReflectionUtils.invokeMethod(tableInfo1.getTable(), setParentMethodName, childMappedByField.getType(), tmp, result);
                                objects.add(tmp);
                            }
                        } finally {
                            if (resultSet2 != null) {
                                resultSet2.close();
                            }
                        }
                        String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), methodName, null);
                        ((List) method.invoke(result)).addAll(objects);
                    }
                }
                return result;
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
            connectionSource.releaseConnection(connection);
        }
    }

    public void fillManyToMany(TableInfo<?> tableInfo, TableInfo<?> tableInfo1, Field field, Object result) throws SQLException {
        try {
            String manyToManyTableName = TableUtils.getManyToManyRelationTableName(connectionSource, tableInfo.getTableName(), tableInfo1.getTableName());
            String fieldId1 = manyToManyTableName.substring(0, manyToManyTableName.indexOf("_")) + "_id";
            String fieldId2 = manyToManyTableName.substring(manyToManyTableName.indexOf("_") + 1) + "_id";

            if (manyToManyTableName.endsWith(tableInfo.getTableName())) {
                String tmp = fieldId1;

                fieldId1 = fieldId2;
                fieldId2 = tmp;
            }
            String sql = "SELECT " + fieldId2 + " FROM " + manyToManyTableName + " WHERE " + fieldId1 + "=" + getIdValue(tableInfo, result);

            Statement statement = null;
            Connection connection = connectionSource.getConnection();
            ResultSet resultSet = null;

            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);

                while (resultSet.next()) {
                    Object tmp = queryForId(tableInfo1, resultSet.getLong(fieldId2));
                    Field manyToManyField = tableInfo1.getFieldByNameInManyToManyRelation(field.getAnnotation(ManyToMany.class).mappedBy());
                    String manyToManyFieldName = manyToManyField.getName();
                    Method methodRelations1 = ReflectionUtils.getDeclaredMethod(tableInfo1.getTable(), "get" + manyToManyFieldName.substring(0, 1).toUpperCase() + manyToManyFieldName.substring(1), null);

                    ((List<Object>) methodRelations1.invoke(tmp)).add(result);
                    Method methodRelations2 = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), null);

                    ((List<Object>) methodRelations2.invoke(result)).add(tmp);
                }
            } finally {
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                connectionSource.releaseConnection(connection);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public void create(Object object) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();
        TableInfo tableInfo = new TableInfo(object.getClass());

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("INSERT INTO ").append(tableInfo.getTableName()).append("(");
            for (Field field : (List<Field>) tableInfo.getFields()) {
                sb.append(field.getAnnotation(DBField.class).fieldName()).append(",");
            }
            for (Field field : (List<Field>) tableInfo.getOneToOneRelations()) {
                sb.append(field.getAnnotation(DBField.class).fieldName()).append(",");
            }
            for (Field field : (List<Field>) tableInfo.getManyToOneRelations()) {
                sb.append(field.getAnnotation(DBField.class).fieldName()).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
            sb.append(" VALUES(");
            for (Field field : (List<Field>) tableInfo.getFields()) {
                String fieldName = field.getName();
                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    sb.append("'").append(method.invoke(object)).append("'").append(",");
                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
            for (Field field : (List<Field>) tableInfo.getOneToOneRelations()) {
                String fieldName = field.getName();

                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    Object foreignObject = method.invoke(object);
                    appendForeignObjectId(sb, foreignObject);
                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
            for (Field field : (List<Field>) tableInfo.getManyToOneRelations()) {
                String fieldName = field.getName();

                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    Object foreignObject = method.invoke(object);
                    appendForeignObjectId(sb, foreignObject);
                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
            statement.execute(sb.toString());
            Field idField = tableInfo.getId();

            if (idField != null) {
                int lastInsertRowId = getLastInsertRowId(statement);
                String fieldName = idField.getName();

                try {
                    String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), methodName, long.class);
                    method.invoke(object, lastInsertRowId);
                } catch (Exception ignore) {
                    throw new SQLException("No setter for " + fieldName);
                }
            }
            //Для сетинга зависимого объекта при связи ManyToOne
            for (Field field : (List<Field>) tableInfo.getManyToOneRelations()) {
                String fieldName = field.getName();

                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    Object foreignObject = method.invoke(object);
                    TableInfo<?> tableInfo1 = new TableInfo<>(foreignObject.getClass());
                    Field mappedBy = tableInfo1.getFieldByMappedNameInOneToManyRelation(fieldName);
                    String mappedByFieldName = mappedBy.getName();
                    String methodName = "get" + mappedByFieldName.substring(0, 1).toUpperCase() + mappedByFieldName.substring(1);

                    method = ReflectionUtils.getDeclaredMethod(foreignObject.getClass(), methodName, null);
                    ((List<Object>) method.invoke(foreignObject)).add(object);
                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
            for (Field field : (List<Field>) tableInfo.getManyToManyRelations()) {
                TableInfo tableInfo1 = new TableInfo(ReflectionUtils.getCollectionGenericClass(field));
                TableUtils.createManyToManyTable(connectionSource, tableInfo, tableInfo1);
                String fieldName = field.getName();

                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    List<Object> objects = ((List) method.invoke(object));
                    Long id = getIdValue(tableInfo, object);

                    for (Object relationObject : objects) {
                        Long idForeign = getIdValue(tableInfo1, relationObject);

                        insertManyToManyRelation(id, idForeign, tableInfo, tableInfo1);
                        Field manyToManyField = tableInfo1.getFieldByNameInManyToManyRelation(field.getAnnotation(ManyToMany.class).mappedBy());
                        String manyToManyFieldName = manyToManyField.getName();

                        try {
                            Method methodRelations = ReflectionUtils.getDeclaredMethod(tableInfo1.getTable(), "get" + manyToManyFieldName.substring(0, 1).toUpperCase() + manyToManyFieldName.substring(1), null);

                            ((List<Object>) methodRelations.invoke(relationObject)).add(object);
                        } catch (Exception ignore) {
                            throw new SQLException("No getter for " + fieldName);
                        }
                    }

                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
        } finally

        {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }

    }

    private void appendForeignObjectId(StringBuilder sb, Object foreignObject) throws Exception {
        TableInfo<?> tableInfo1 = new TableInfo<>(foreignObject.getClass());
        String idFieldName = tableInfo1.getId().getName();
        String methodName = "get" + idFieldName.substring(0, 1).toUpperCase() + idFieldName.substring(1);

        Method method = ReflectionUtils.getDeclaredMethod(foreignObject.getClass(), methodName, null);
        sb.append(method.invoke(foreignObject)).append(",");
    }

    private int getLastInsertRowId(Statement statement) throws SQLException {
        String lastInsertRowId = "SELECT last_insert_rowid() AS last_id";
        ResultSet resultSet = null;

        try {
            resultSet = statement.executeQuery(lastInsertRowId);
            resultSet.next();
            int lastId = resultSet.getInt("last_id");

            return lastId;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    private void insertManyToManyRelation(long id1, long id2, TableInfo tableInfo, TableInfo tableInfo1) throws SQLException {
        String manyToManyTableName = TableUtils.getManyToManyRelationTableName(connectionSource, tableInfo.getTableName(), tableInfo1.getTableName());
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

        execute(connectionSource, sql);
    }

    private boolean isExistRelation(long id1, long id2, String tableName) throws SQLException {
        String fieldId1 = tableName.substring(0, tableName.indexOf("_")) + "_id";
        String fieldId2 = tableName.substring(tableName.indexOf("_") + 1) + "_id";
        String sql = "SELECT * from " + tableName + " WHERE " + fieldId1 + "=" + id1 + " AND " + fieldId2 + "=" + id2;

        Statement statement = null;
        Connection connection = connectionSource.getConnection();
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                return true;
            }
            return false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            connectionSource.releaseConnection(connection);
        }
    }

    public long getIdValue(TableInfo tableInfo, Object object) throws Exception {
        Field idField = tableInfo.getId();
        String idFieldName = idField.getName();
        String methodName = "get" + idFieldName.substring(0, 1).toUpperCase() + idFieldName.substring(1);
        Method methodGetId = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), methodName, null);

        return (Long) methodGetId.invoke(object);
    }
}
