package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.stament_executor.IStatementExecutor;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.support.WrappedConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TransactionImpl implements Transaction {

    private final IStatementExecutor statementExecutor;

    private final Connection connection;

    private ConnectionSource connectionSource;

    private State state;

    TransactionImpl(IStatementExecutor statementExecutor, ConnectionSource connectionSource) throws SQLException {
        this.statementExecutor = statementExecutor;
        this.connection = connectionSource.getConnection();
        this.connectionSource = connectionSource;
    }

    @Override
    public<T> int create(Collection<T> objects) throws SQLException {
        check();
        return statementExecutor.create(connection, objects);
    }

    @Override
    public<T> int create(T object) throws SQLException {
        check();
        return statementExecutor.create(connection, object);
    }

    @Override
    public<T> boolean createTable(Class<T> tClass, boolean ifNotExists) throws SQLException {
        check();
        return statementExecutor.createTable(connection, tClass, ifNotExists);
    }

    @Override
    public<T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException {
        check();
        return statementExecutor.queryForId(connection, tClass, id);
    }

    @Override
    public<T> List<T> queryForAll(Class<T> tClass) throws SQLException {
        check();
        return statementExecutor.queryForAll(connection, tClass);
    }

    @Override
    public<T> int update(T object) throws SQLException {
        check();
        return statementExecutor.update(connection, object);
    }

    @Override
    public<T> int delete(T object) throws SQLException {
        check();
        return statementExecutor.delete(connection, object);
    }

    @Override
    public<T, ID> int deleteById(Class<T> tClass, ID id) throws SQLException {
        check();
        return statementExecutor.deleteById(connection, tClass, id);
    }

    @Override
    public<T> boolean dropTable(Class<T> tClass, boolean ifExists) throws SQLException {
        check();
        return statementExecutor.dropTable(connection, tClass, ifExists);
    }

    @Override
    public<T> void createIndexes(Class<T> tClass) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public<T> void dropIndexes(Class<T> tClass) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public<T> long countOff(Class<T> tClass) throws SQLException {
        check();
        return statementExecutor.countOff(connection, tClass);
    }

    @Override
    public long queryForLong(String query) throws SQLException {
        check();
        return statementExecutor.queryForLong(connection, query);
    }

    @Override
    public <R> GenericResults<R> query(String query) throws SQLException {
        check();
        return statementExecutor.query(new WrappedConnectionSource(connectionSource) {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public void releaseConnection(Connection connection) throws SQLException {
            }
        }, null, null, query);
    }

    @Override
    public <R> GenericResults<R> query(Class<R> fromTable, String query) throws SQLException {
        check();
        return statementExecutor.query(new WrappedConnectionSource(connectionSource) {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public void releaseConnection(Connection connection) throws SQLException {
            }
        }, fromTable, null, query);
    }

    @Override
    public <R> GenericResults<R> query(String query, Map<Integer, Object> args) throws SQLException {
        check();
        return statementExecutor.query(new WrappedConnectionSource(connectionSource) {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public void releaseConnection(Connection connection) throws SQLException {
            }
        }, null, args, query);
    }

    @Override
    public <R> GenericResults<R> query(Class<R> resultClass, String query, Map<Integer, Object> args) throws SQLException {
        check();
        return statementExecutor.query(new WrappedConnectionSource(connectionSource) {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public void releaseConnection(Connection connection) throws SQLException {
            }
        }, resultClass, args, query);
    }

    @Override
    public<R> GenericResults<R> query(SelectStatement<R> statement) throws SQLException {
        check();
        return statementExecutor.query(new WrappedConnectionSource(connectionSource) {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public void releaseConnection(Connection connection) throws SQLException {
            }
        }, statement);
    }

    @Override
    public void begin() throws SQLException {
        state = State.BEGIN;
        connection.setAutoCommit(false);
    }

    @Override
    public void commit() throws SQLException {
        checkTransactionState();
        connection.commit();
        state = State.COMMIT;
        connectionSource.releaseConnection(connection);
    }

    @Override
    public void rollback() throws SQLException {
        checkTransactionState();
        connection.rollback();
        state = State.ROLLBACK;
        connectionSource.releaseConnection(connection);
    }

    private void check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is invalid");
        }
    }

    private void checkTransactionState() throws SQLException {
        if (!state.equals(State.BEGIN)) {
            throw new SQLException("Transaction not begined");
        }
    }

    private enum State {
        BEGIN,
        COMMIT,
        ROLLBACK
    }
}
