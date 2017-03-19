package dao;

import clause.QueryBuilder;
import clause.Update;
import clause.Where;
import field.DBField;
import field.DataType;
import field.OneToOne;
import support.JDBCConnectionSource;
import table.DBTable;
import table.TableInfo;
import utils.ReflectionUtils;
import utils.StatementExecutor;
import utils.TableUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public class DaoImpl<T> implements Dao<T> {

    private JDBCConnectionSource connectionSource;
    private final TableInfo<T> tableInfo;
    private StatementExecutor<T> statementExecutor;

    public DaoImpl(JDBCConnectionSource connectionSource, Class<T> dbTable) {
        this.connectionSource = connectionSource;
        this.tableInfo = new TableInfo<>(dbTable);
        this.statementExecutor = new StatementExecutor<>(connectionSource);
    }

    public void create(T object) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("INSERT INTO ").append(tableInfo.getTableName()).append("(");
            for (Field field : tableInfo.getFields()) {
                sb.append(field.getAnnotation(DBField.class).fieldName()).append(",");
            }
            for (Field field : tableInfo.getOneToOneRelations()) {
                sb.append(field.getAnnotation(DBField.class).fieldName()).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
            sb.append(" VALUES(");
            for (Field field : tableInfo.getFields()) {
                String fieldName = field.getName();
                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    sb.append("'").append(method.invoke(object)).append("'").append(",");
                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
            for (Field field : tableInfo.getOneToOneRelations()) {
                String fieldName = field.getName();

                try {
                    Method method = ReflectionUtils.getDeclaredMethod(tableInfo.getTable(), "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), null);
                    Object foreignObject = method.invoke(object);
                    TableInfo<?> tableInfo = new TableInfo<>(foreignObject.getClass());
                    String idFieldName = tableInfo.getId().getName();
                    String methodName = "get" + idFieldName.substring(0, 1).toUpperCase() + idFieldName.substring(1);
                    method = ReflectionUtils.getDeclaredMethod(foreignObject.getClass(), methodName, null);
                    sb.append(method.invoke(foreignObject)).append(",");
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
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }
    }

    @Override
    public T queryForId(long id) throws SQLException {
        return (T) statementExecutor.queryForId(tableInfo, id);
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            String queryForAll = "SELECT * FROM " + tableInfo.getTableName();
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery(queryForAll);
                List<Field> fields = tableInfo.getFields();
                fields.add(tableInfo.getId());
                List<T> result = new ArrayList<>();

                while (resultSet.next()) {
                    T object = tableInfo.getTable().newInstance();

                    for (Field field : fields) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, resultSet.getObject(field.getAnnotation(DBField.class).fieldName()));
                    }
                    for (Field field: tableInfo.getOneToOneRelations()) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, resultSet.getObject(field.getAnnotation(DBField.class).fieldName()));

                    }
                    result.add(object);
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
            connectionSource.releaseConnection(connection);
        }
    }

    @Override
    public List<T> queryForAll(String sql) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery(sql);
                List<Field> fields = tableInfo.getFields();
                List<T> result = new ArrayList<>();

                while (resultSet.next()) {
                    T object = tableInfo.getTable().newInstance();

                    for (Field field : fields) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, resultSet.getObject(field.getAnnotation(DBField.class).fieldName()));
                    }
                    result.add(object);
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
            connectionSource.releaseConnection(connection);
        }
    }

    @Override
    public void update(T object) {

    }

    @Override
    public void delete(T object) {

    }

    @Override
    public List<T> queryForWhere(Where where) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("SELECT * FROM ").append(tableInfo.getTableName()).append(" ").append(where.toString());
            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery(sb.toString());
                List<Field> fields = tableInfo.getFields();
                fields.add(tableInfo.getId());
                List<T> result = new ArrayList<>();

                while (resultSet.next()) {
                    T object = tableInfo.getTable().newInstance();

                    for (Field field : fields) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, resultSet.getObject(field.getAnnotation(DBField.class).fieldName()));
                    }
                    result.add(object);
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
            connectionSource.releaseConnection(connection);
        }
    }

    @Override
    public int queryForUpdate(Update update) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("UPDATE ").append(tableInfo.getTableName()).append(" ").append(update.toString());

            return statement.executeUpdate(sb.toString());
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }
    }

    @Override
    public boolean deleteForWhere(Where where) throws SQLException {
        Statement statement = null;
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
        }
    }

    @Override
    public QueryBuilder<T> queryBuilder() {
        return new QueryBuilder<>(tableInfo.getTable(), this);
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
}
