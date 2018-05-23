package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.stamentexecutor.Argument;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultsImpl;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Default database engine.
 */
public class DefaultDatabaseEngine implements DatabaseEngine {

    /**
     * Database type.
     * @see DatabaseType
     */
    private final DatabaseType databaseType;

    /**
     * Create new instance.
     * @param databaseType target database type
     */
    public DefaultDatabaseEngine(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public DatabaseResults select(DatabaseConnection<?> databaseConnection,
                                  Select select,
                                  Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(select);
        Connection connection = databaseConnection.getWrappedConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        processArgs(preparedStatement, args);
        ResultSet resultSet = preparedStatement.executeQuery();

        return new DatabaseResultsImpl(resultSet, resultSet.getMetaData()) {
            @Override
            public void close() throws SQLException {
                resultSet.close();
                preparedStatement.close();
            }
        };
    }

    @Override
    public DatabaseResults create(DatabaseConnection<?> databaseConnection,
                                  CreateQuery createQuery,
                                  Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(createQuery);
        Connection connection = databaseConnection.getWrappedConnection();

        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        processArgs(statement, args);
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();

        return new DatabaseResultsImpl(resultSet, resultSet.getMetaData()) {
            @Override
            public void close() throws SQLException {
                resultSet.close();
                statement.close();
            }
        };
    }

    @Override
    public void delete(DatabaseConnection<?> databaseConnection,
                       DeleteQuery deleteQuery,
                       Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(deleteQuery);
        Connection originialConnection = databaseConnection.getWrappedConnection();

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            processArgs(preparedQuery, args);

            preparedQuery.executeUpdate();
        }
    }

    @Override
    public void update(DatabaseConnection<?> databaseConnection,
                       UpdateQuery updateQuery,
                       Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(updateQuery);
        Connection originialConnection = databaseConnection.getWrappedConnection();

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            processArgs(preparedQuery, args);

            preparedQuery.executeUpdate();
        }
    }

    @Override
    public boolean createTable(DatabaseConnection<?> databaseConnection,
                               CreateTableQuery createTableQuery) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(createTableQuery));

            return true;
        }
    }

    @Override
    public boolean dropTable(DatabaseConnection<?> databaseConnection,
                             DropTableQuery dropTableQuery) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(dropTableQuery));

            return true;
        }
    }

    @Override
    public void createIndex(DatabaseConnection<?> databaseConnection,
                            CreateIndexQuery createIndexQuery) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(createIndexQuery));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void dropIndex(DatabaseConnection<?> databaseConnection,
                          DropIndexQuery dropIndexQuery) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(dropIndexQuery));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Set args to requested prepared statement.
     * @param preparedStatement target prepared statement
     * @param args target args
     * @throws SQLException on any SQL problems
     */
    private void processArgs(PreparedStatement preparedStatement,
                             Map<Integer, Argument> args) throws SQLException {
        for (Map.Entry<Integer, Argument> entry : args.entrySet()) {
            Argument argument = entry.getValue();
            Object value = argument.getValue();

            if (argument.getConverter().isPresent()) {
                List<Converter<?, Object>> converters = argument.getConverter().get();

                for (Converter converter : converters) {
                    value = converter.javaToSql(value);
                }
            }
            argument.getDataPersister().setObject(preparedStatement, entry.getKey(), value);
        }
    }

    /**
     * Make query by visitor. This engine use {@link DefaultVisitor}.
     * @param queryElement target visitor element
     * @return sql query
     */
    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }

}
