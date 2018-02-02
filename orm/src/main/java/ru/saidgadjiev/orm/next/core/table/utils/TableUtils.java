package ru.saidgadjiev.orm.next.core.table.utils;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.query.core.DropTableQuery;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.stament_executor.PreparedQueryImpl;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by said on 28.01.2018.
 */
public class TableUtils {

    private TableUtils() {
    }

    public static boolean dropTable(ConnectionSource connectionSource,
                                    TableInfo<?> tableInfo,
                                    boolean ignoreErrors) throws SQLException {
        DropTableQuery dropTableQuery = DropTableQuery.buildQuery(tableInfo.getTableName(), false);
        Connection connection = connectionSource.getConnection();

        try {
            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(dropTableQuery, connectionSource.getDatabaseType())))) {
                preparedQuery.executeUpdate();

                return true;
            }
        } catch (SQLException ex) {
            if (!ignoreErrors) {
                throw ex;
            }
        } finally {
            connectionSource.releaseConnection(connection);
        }

        return false;
    }

    private static String getQuery(QueryElement queryElement, DatabaseType databaseType) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
