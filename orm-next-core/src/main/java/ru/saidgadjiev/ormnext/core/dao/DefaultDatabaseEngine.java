package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResultsImpl;
import ru.saidgadjiev.ormnext.core.database_type.DatabaseType;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.data_persister.DataPersister;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query_element.*;
import ru.saidgadjiev.ormnext.core.loader.visitor.DefaultVisitor;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.loader.GeneratedKey;

import java.sql.*;
import java.util.Map;

/**
 * Default database engine.
 */
public class DefaultDatabaseEngine implements DatabaseEngine<Connection> {

    /**
     * Generated key column index.
     */
    private static final int GENERATED_KEY_COLUMN_INDEX = 1;

    /**
     * Database type.
     *
     * @see DatabaseType
     */
    private final DatabaseType databaseType;

    /**
     * Create a new instance.
     *
     * @param databaseType target database type
     */
    public DefaultDatabaseEngine(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public DatabaseResults select(DatabaseConnection<Connection> databaseConnection,
                                  Select select,
                                  Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(select);
        Connection connection = databaseConnection.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        try {
            processArgs(preparedStatement, args);
            ResultSet resultSet = preparedStatement.executeQuery();

            return new DatabaseResultsImpl(resultSet) {
                @Override
                public void close() throws SQLException {
                    try {
                        resultSet.close();
                    } finally {
                        preparedStatement.close();
                    }
                }
            };
        } catch (SQLException ex) {
            preparedStatement.close();
            throw ex;
        }
    }

    @Override
    public void create(DatabaseConnection<Connection> databaseConnection,
                       CreateQuery createQuery,
                       Map<Integer, Argument> args,
                       IDatabaseColumnType primaryKey,
                       GeneratedKey generatedKey) throws SQLException {
        String query = getQuery(createQuery);
        Connection connection = databaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            processArgs(statement, args);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                while (resultSet.next()) {
                    generatedKey.set(readValue(resultSet, resultSetMetaData.getColumnType(GENERATED_KEY_COLUMN_INDEX)));
                }
            }
        }
    }

    /**
     * Read value by sql type. Use for read generated key.
     *
     * @param resultSet target results
     * @param sqlType   target sql type {@link Types}
     * @return read value
     * @throws SQLException any SQL exceptions
     */
    private Number readValue(ResultSet resultSet, int sqlType) throws SQLException {
        switch (sqlType) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.NUMERIC:
                return resultSet.getLong(GENERATED_KEY_COLUMN_INDEX);
            case Types.INTEGER:
                return resultSet.getInt(GENERATED_KEY_COLUMN_INDEX);
            default:
                throw new SQLException(
                        "Unknown DataType for typeVal " + sqlType + " in column " + GENERATED_KEY_COLUMN_INDEX
                );
        }
    }

    @Override
    public void delete(DatabaseConnection<Connection> databaseConnection,
                       DeleteQuery deleteQuery,
                       Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(deleteQuery);
        Connection originialConnection = databaseConnection.getConnection();

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            processArgs(preparedQuery, args);

            preparedQuery.executeUpdate();
        }
    }

    @Override
    public void update(DatabaseConnection<Connection> databaseConnection,
                       UpdateQuery updateQuery,
                       Map<Integer, Argument> args) throws SQLException {
        String query = getQuery(updateQuery);
        Connection originialConnection = databaseConnection.getConnection();

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            processArgs(preparedQuery, args);

            preparedQuery.executeUpdate();
        }
    }

    @Override
    public boolean createTable(DatabaseConnection<Connection> databaseConnection,
                               CreateTableQuery createTableQuery) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(createTableQuery));

            return true;
        }
    }

    @Override
    public boolean dropTable(DatabaseConnection<Connection> databaseConnection,
                             DropTableQuery dropTableQuery) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(dropTableQuery));

            return true;
        }
    }

    @Override
    public void createIndex(DatabaseConnection<Connection> databaseConnection,
                            CreateIndexQuery createIndexQuery) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(createIndexQuery));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void dropIndex(DatabaseConnection<Connection> databaseConnection,
                          DropIndexQuery dropIndexQuery) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(dropIndexQuery));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Set args to requested prepared statement.
     *
     * @param preparedStatement target prepared statement
     * @param args              target args
     * @throws SQLException on any SQL problems
     */
    private void processArgs(PreparedStatement preparedStatement,
                             Map<Integer, Argument> args) throws SQLException {
        for (Map.Entry<Integer, Argument> entry : args.entrySet()) {
            Argument argument = entry.getValue();
            Object value = argument.getValue();
            DataPersister dataPersister = DataPersisterManager.lookup(argument.getDataType());

            dataPersister.setObject(preparedStatement, entry.getKey(), value);
        }
    }

    /**
     * Make query by visitor. This engine use {@link DefaultVisitor}.
     *
     * @param queryElement target visitor element
     * @return sql query
     */
    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
