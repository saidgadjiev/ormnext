package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.stamentexecutor.Argument;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSetImpl;

import java.sql.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDatabaseEngine implements DatabaseEngine {

    private final DatabaseType databaseType;

    public DefaultDatabaseEngine(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public void select(DatabaseConnection<?> databaseConnection, Select select, Collection<Argument> args, ResultSetProcessor resultSetProcessor) throws SQLException {
        String query = getQuery(select);
        Connection connection = databaseConnection.getWrappedConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            processArgs(preparedStatement, args);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSetProcessor.process(new DatabaseResultSetImpl(resultSet, resultSet.getMetaData()));
            }
        }
    }

    @Override
    public void create(DatabaseConnection<?> databaseConnection, CreateQuery createQuery, Collection<Argument> args, ResultSetProcessor resultSetProcessor) throws SQLException {
        String query = getQuery(createQuery);
        Connection connection = databaseConnection.getWrappedConnection();

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            processArgs(statement, args);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSetProcessor.process(new DatabaseResultSetImpl(resultSet, resultSet.getMetaData()));
            }
        }
    }

    @Override
    public void delete(DatabaseConnection<?> databaseConnection, DeleteQuery deleteQuery, Collection<Argument> args) throws SQLException {
        String query = getQuery(deleteQuery);
        Connection originialConnection = databaseConnection.getWrappedConnection();

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            processArgs(preparedQuery, args);

            preparedQuery.executeUpdate();
        }
    }

    @Override
    public void update(DatabaseConnection<?> databaseConnection, UpdateQuery updateQuery, Collection<Argument> args) throws SQLException {
        String query = getQuery(updateQuery);
        Connection originialConnection = databaseConnection.getWrappedConnection();

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            processArgs(preparedQuery, args);

            preparedQuery.executeUpdate();
        }
    }

    @Override
    public boolean createTable(DatabaseConnection<?> databaseConnection, CreateTableQuery createTableQuery) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(createTableQuery));

            return true;
        }
    }

    @Override
    public boolean dropTable(DatabaseConnection<?> databaseConnection, DropTableQuery dropTableQuery) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        try (Statement statement = connection.createStatement()) {
            statement.execute(getQuery(dropTableQuery));

            return true;
        }
    }

    @Override
    public void createIndexes(DatabaseConnection<?> databaseConnection, Iterator<CreateIndexQuery> indexQueryIterator) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        while (indexQueryIterator.hasNext()) {
            CreateIndexQuery createIndexQuery = indexQueryIterator.next();

            try (Statement statement = connection.createStatement()) {
                statement.execute(getQuery(createIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public void dropIndexes(DatabaseConnection<?> databaseConnection, Iterator<DropIndexQuery> dropIndexQueryIterator) throws SQLException {
        Connection connection = databaseConnection.getWrappedConnection();

        while (dropIndexQueryIterator.hasNext()) {
            DropIndexQuery dropIndexQuery = dropIndexQueryIterator.next();

            try (Statement statement = connection.createStatement()) {
                statement.execute(getQuery(dropIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    private void processArgs(PreparedStatement preparedStatement, Collection<Argument> args) throws SQLException {
        AtomicInteger index = new AtomicInteger();

        for (Argument argument: args) {
            Object value = argument.getValue();

            if (argument.getConverter().isPresent()) {
                List<Converter<?, Object>> converters = argument.getConverter().get();

                for (Converter converter: converters) {
                    value = converter.javaToSql(value);
                }
            }
            argument.getDataPersister().setObject(preparedStatement, index.incrementAndGet(), value);
        }
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }

}
