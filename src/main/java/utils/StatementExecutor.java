package utils;

import clause.Where;
import field.DBField;
import field.ManyToOne;
import field.OneToMany;
import org.apache.log4j.Logger;
import org.sqlite.core.DB;
import support.JDBCConnectionSource;
import table.DBTable;
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
                    for (Field field: tableInfo.getOneToManyRelations()) {
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
}
