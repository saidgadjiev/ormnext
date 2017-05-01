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
import java.lang.reflect.InvocationTargetException;
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
    private StatementExecutor statementExecutor;

    public DaoImpl(JDBCConnectionSource connectionSource, Class<T> dbTable) {
        this.connectionSource = connectionSource;
        this.tableInfo = new TableInfo<>(dbTable);
        this.statementExecutor = new StatementExecutor(connectionSource);
    }

    public void create(T object) throws SQLException {
        statementExecutor.create(object);
    }

    @Override
    public T queryForId(long id) throws SQLException {
        T result = (T) statementExecutor.queryForId(tableInfo, id);
        for (Field field: tableInfo.getManyToManyRelations()) {
            statementExecutor.fillManyToMany(tableInfo, new TableInfo<>(ReflectionUtils.getCollectionGenericClass(field)), field, result);
        }

        return result;
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
                    for (Field field : tableInfo.getOneToOneRelations()) {
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

}
