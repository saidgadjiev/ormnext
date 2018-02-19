package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.stament_executor.IStatementExecutor;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class TransactionImpl implements Transaction {

    private final IStatementExecutor statementExecutor;

    private final Connection connection;

    private Callable<Void> close;

    private State state;

    public TransactionImpl(IStatementExecutor statementExecutor, Connection connection, Callable<Void> close) {
        this.statementExecutor = statementExecutor;
        this.connection = connection;
        this.close = close;
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
    public<T, ID> T queryForId(ID id, Class<T> tClass) throws SQLException {
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
    public<T, ID> int deleteById(ID id, Class<T> tClass) throws SQLException {
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
        return statementExecutor.countOff(connection, tClass);
    }

    @Override
    public <R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper) throws SQLException {
        check();
        return statementExecutor.query(connection, query, resultsMapper);
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
    }

    @Override
    public void rollback() throws SQLException {
        checkTransactionState();
        connection.rollback();
        state = State.ROLLBACK;
    }

    private void check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is invalid");
        }
    }

    @Override
    public void close() throws Exception{
        close.call();
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
