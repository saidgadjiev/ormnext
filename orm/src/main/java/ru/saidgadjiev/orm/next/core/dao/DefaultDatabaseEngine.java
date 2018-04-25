package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.support.DatabaseConnection;
import ru.saidgadjiev.orm.next.core.support.OrmNextResultSet;

import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDatabaseEngine implements DatabaseEngine {

    private final DatabaseType databaseType;

    private static final Integer GENERATED_CLUMN_INDEX = 1;

    public DefaultDatabaseEngine(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public void select(DatabaseConnection connection, Select select, List<Object> args, ResultSetProcessor resultSetProcessor) throws SQLException {
        String query = getQuery(select);
        Connection originialConnection = (Connection) connection.getConnection();

        try (PreparedStatement preparedStatement = originialConnection.prepareStatement(query)) {
            setArgs(preparedStatement, args);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSetProcessor.process(new OrmNextResultSet() {
                    @Override
                    public boolean next() throws SQLException {
                        return resultSet.next();
                    }

                    @Override
                    public Object getObject(String columnName) throws SQLException {
                        return resultSet.getObject(columnName);
                    }

                    @Override
                    public Object getObject(int columnId) throws SQLException {
                        return resultSet.getObject(columnId);
                    }

                    @Override
                    public OrmNextMetaData getMetaData() {
                        return null;
                    }

                    @Override
                    public GeneratedKeys generatedKeys() {
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public int create(DatabaseConnection databaseConnection, CreateQuery createQuery, List<Object> args, ResultSetProcessor resultSetProcessor) throws SQLException {
        Connection connection = (Connection) databaseConnection.getConnection();
        String query = getQuery(createQuery);

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setArgs(statement, args);
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                resultSetProcessor.process(new OrmNextResultSet() {
                    @Override
                    public boolean next() throws SQLException {
                        return resultSet.next();
                    }

                    @Override
                    public Object getObject(String columnName) throws SQLException {
                        return resultSet.getObject(columnName);
                    }

                    @Override
                    public Object getObject(int columnId) throws SQLException {
                        return resultSet.getObject(columnId);
                    }

                    @Override
                    public OrmNextMetaData getMetaData() {
                        return resultSetMetaData::getColumnType;
                    }

                    @Override
                    public GeneratedKeys generatedKeys() {
                        return new GeneratedKeys() {

                            @Override
                            public boolean next() throws SQLException {
                                return resultSet.next();
                            }

                            @Override
                            public Object getGeneratedKey() throws SQLException {
                                return resultSet.getObject(GENERATED_CLUMN_INDEX);
                            }

                            @Override
                            public int getType() throws SQLException {
                                return resultSetMetaData.getColumnType(GENERATED_CLUMN_INDEX);
                            }
                        };
                    }
                });

                return statement.executeUpdate();
            }
        }
    }

    @Override
    public int delete(DatabaseConnection connection, DeleteQuery deleteQuery, List<Object> args) throws SQLException {
        Connection originialConnection = (Connection) connection.getConnection();
        String query = getQuery(deleteQuery);

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            setArgs(preparedQuery, args);

            return preparedQuery.executeUpdate();
        }
    }

    @Override
    public int update(DatabaseConnection connection, UpdateQuery updateQuery, List<Object> args) throws SQLException {
        Connection originialConnection = (Connection) connection.getConnection();
        String query = getQuery(updateQuery);

        try (PreparedStatement preparedQuery = originialConnection.prepareStatement(query)) {
            setArgs(preparedQuery, args);

            return preparedQuery.executeUpdate();
        }
    }

    @Override
    public boolean createTable(DatabaseConnection databaseConnection, CreateTableQuery createTableQuery) throws SQLException {
        Connection connection = (Connection) databaseConnection.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(getQuery(createTableQuery));

            return true;
        }
    }

    @Override
    public boolean dropTable(DatabaseConnection databaseConnection, DropTableQuery dropTableQuery) throws SQLException {
        Connection connection = (Connection) databaseConnection.getConnection();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(getQuery(dropTableQuery));

            return true;
        }
    }

    @Override
    public void createIndexes(DatabaseConnection databaseConnection, Iterator<CreateIndexQuery> indexQueryIterator) throws SQLException {
        Connection connection = (Connection) databaseConnection.getConnection();

        while (indexQueryIterator.hasNext()) {
            CreateIndexQuery createIndexQuery = indexQueryIterator.next();

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(getQuery(createIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public void dropIndexes(DatabaseConnection databaseConnection, Iterator<DropIndexQuery> dropIndexQueryIterator) throws SQLException {
        Connection connection = (Connection) databaseConnection.getConnection();

        while (dropIndexQueryIterator.hasNext()) {
            DropIndexQuery dropIndexQuery = dropIndexQueryIterator.next();

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(getQuery(dropIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    private void setArgs(PreparedStatement preparedStatement, List<Object> args) throws SQLException {
        if (args == null) {
            return;
        }
        AtomicInteger integer = new AtomicInteger();

        for (Object arg: args) {
            preparedStatement.setObject(integer.incrementAndGet(), arg);
        }
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }

}
