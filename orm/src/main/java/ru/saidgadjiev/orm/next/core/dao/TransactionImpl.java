package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.dao.transaction.Transaction;
import ru.saidgadjiev.orm.next.core.stamentexecutor.DefaultEntityLoader;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class TransactionImpl implements Transaction {

    private final DefaultEntityLoader statementExecutor;

    private final Connection connection;

    private ConnectionSource connectionSource;

    private State state;

    TransactionImpl(DefaultEntityLoader statementExecutor, ConnectionSource connectionSource) throws SQLException {
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
    public void begin() throws SQLException {
        state = State.BEGIN;
        connection.setAutoCommit(false);
    }

    @Override
    public void commit() throws SQLException {
        checkTransactionState();
        try {
            connection.commit();
            state = State.COMMIT;
        } finally {
            connection.setAutoCommit(true);
        }

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
        checkTransactionState();
    }

    private void checkTransactionState() throws SQLException {
        if (state == null || !state.equals(State.BEGIN)) {
            throw new SQLException("Transaction not begined");
        }
    }

    private enum State {
        BEGIN,
        COMMIT,
        ROLLBACK
    }
}
