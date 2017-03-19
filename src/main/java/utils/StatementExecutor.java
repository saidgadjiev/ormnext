package utils;

import clause.Where;
import field.DBField;
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
public class StatementExecutor<T> {

    private JDBCConnectionSource connectionSource;

    public StatementExecutor(JDBCConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
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
                    for (Field field: tableInfo.getOneToOneRelations()) {
                        String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                        TableInfo<?> tableInfo1 = new TableInfo<>(field.getType());
                        Object tmp = queryForId(tableInfo1, resultSet.getLong(tableInfo.getId().getAnnotation(DBField.class).fieldName()));

                        ReflectionUtils.invokeMethod(tableInfo.getTable(), methodName, field.getType(), result, tmp);
                    }
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
}
