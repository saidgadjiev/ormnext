package dao;

import support.JDBCConnectionSource;
import field.DBField;
import table.DBTable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public class DaoImpl<T> implements Dao<T> {

    private JDBCConnectionSource connectionSource;
    private final Class<T> dbTable;

    public DaoImpl(JDBCConnectionSource connectionSource, Class<T> dbTable) {
        this.connectionSource = connectionSource;
        this.dbTable = dbTable;
    }

    public void create(T object) throws SQLException {
        Statement statement = null;
        Connection connection = connectionSource.getConnection();

        try {
            statement = connection.createStatement();
            StringBuilder sb = new StringBuilder();

            sb.append("INSERT INTO ").append(getDBTableName()).append("(");
            Field[] fields = getDBTableFields();
            List<String> fieldsNames = new ArrayList<String>();

            for (Field field : fields) {
                if (field.isAnnotationPresent(DBField.class)) {
                    sb.append(field.getAnnotation(DBField.class).fieldName());
                    fieldsNames.add(field.getName());
                }
            }
            sb.append(")").append(" VALUES(");
            for (String fieldName : fieldsNames) {
                try {
                    Method method = dbTable.getMethod("get" + fieldName);
                    sb.append(method.invoke(object)).append(",");
                } catch (Exception ignore) {
                    throw new SQLException("No getter for " + fieldName);
                }
            }
            sb.replace(sb.length() - 2, sb.length() - 1, ")");
            statement.execute(sb.toString());
        } finally {
            if (statement != null) {
                statement.close();
            }
            connectionSource.releaseConnection(connection);
        }
    }

    private String getDBTableName() {
        return dbTable.getAnnotation(DBTable.class).name();
    }

    private Field[] getDBTableFields() {
        return dbTable.getFields();
    }
}
