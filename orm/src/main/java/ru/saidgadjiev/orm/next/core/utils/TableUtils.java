package ru.saidgadjiev.orm.next.core.utils;

import ru.saidgadjiev.orm.next.core.query.core.CreateTableQuery;
import ru.saidgadjiev.orm.next.core.query.core.DropTableQuery;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.stamentexecutor.IStatement;
import ru.saidgadjiev.orm.next.core.stamentexecutor.StatementImpl;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by said on 24.02.2018.
 */
public class TableUtils {

    private TableUtils() {

    }

    public static <T> boolean createTable(ConnectionSource connectionSource, Class<T> tClass, boolean ifNotExists) throws SQLException {
        try {
            DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);
            CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                    databaseEntityMetadata,
                    ifNotExists
            );
            DefaultVisitor visitor = new DefaultVisitor(connectionSource.getDatabaseType());

            createTableQuery.accept(visitor);

            Connection connection = connectionSource.getConnection();
            try (IStatement statement = new StatementImpl(connection.createStatement())) {
                statement.executeUpdate(visitor.getQuery());

                return true;
            } catch (Exception ex) {
                throw new SQLException(ex);
            } finally {
                connectionSource.releaseConnection(connection);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public static <T> boolean dropTable(ConnectionSource connectionSource, Class<T> tClass, boolean ifExists) throws SQLException {
        try {
            DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);

            DropTableQuery dropTableQuery = DropTableQuery.buildQuery(databaseEntityMetadata.getTableName(), ifExists);
            DefaultVisitor visitor = new DefaultVisitor(connectionSource.getDatabaseType());

            dropTableQuery.accept(visitor);
            try (Connection connection = connectionSource.getConnection();
                 IStatement statement = new StatementImpl(connection.createStatement())) {
                statement.executeUpdate(visitor.getQuery());

                return true;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }
}
