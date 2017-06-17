package utils;

import clause.table.CreateTable;
import dao.visitor.QueryVisitor;
import dao.visitor.QueryVisitorImpl;
import field.FieldWrapper;
import field.TableField;
import table.Table;
import table.TableInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by said on 25.02.17.
 */
public class TableUtils {

    private TableUtils() {
    }

    public static <T> void createTable(DataSource dataSource, Class<T> dbTable) throws SQLException {
        TableInfo tableInfo = new TableInfo(dbTable);

        if (isExistTable(dataSource.getConnection(), tableInfo.getTableName())) {
            return;
        }
        CreateTable createTable = new CreateTable(tableInfo.getTableName());

        for (FieldWrapper wrapper: tableInfo.getFields()) {
            if (wrapper.isAnnotationPresent(TableField.class)) {
                createTable.addFieldWapper(wrapper);
            }
        }
        QueryVisitor visitor = new QueryVisitorImpl();

        createTable.accept(visitor);
        MiamiUtils.execute(dataSource.getConnection(), visitor.preparedQuery());
    }

    public static boolean isExistTable(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
        String name = MiamiUtils.getString(connection, sql, "name");

        return name != null && !name.isEmpty();
    }

    public static void createManyToManyTable(Connection connection, TableInfo tableInfo, TableInfo tableInfo1) throws SQLException {
        String manyToManyTableName1 = tableInfo.getTableName() + "_" + tableInfo1.getTableName();
        String manyToManyTableName2 =  tableInfo1.getTableName() + "_"  + tableInfo.getTableName();

        if (!isExistTable(connection, manyToManyTableName1) && !isExistTable(connection, manyToManyTableName2)) {
            String sql = "CREATE TABLE " + manyToManyTableName1 + "(" + tableInfo.getTableName() + "_id INTEGER NOT NULL, " + tableInfo1.getTableName() + "_id INTEGER NOT NULL)";

            MiamiUtils.execute(connection, sql);
        }
    }

    public static String getManyToManyRelationTableName(Connection connectionSource, String table1, String table2) throws SQLException {
        if (TableUtils.isExistTable(connectionSource, table1 + "_" + table2)) {
            return table1 + "_" + table2;
        }
        if (TableUtils.isExistTable(connectionSource, table2 + "_" + table1)) {
            return table2 + "_" + table1;
        }

        throw new SQLException("Relation table not found");
    }

    public static <T> void dropTable(Connection connection, Class<T> dbTable) throws SQLException {
        String dropTableSQL = "DROP TABLE " + dbTable.getAnnotation(Table.class).name();
        MiamiUtils.execute(connection, dropTableSQL);
    }

    public static <T> void clearTable(Connection connection, Class<T> dbTable) throws SQLException {
        String truncateTableSQL = "DELETE FROM " + dbTable.getAnnotation(Table.class).name();
        MiamiUtils.execute(connection, truncateTableSQL);
    }
}
