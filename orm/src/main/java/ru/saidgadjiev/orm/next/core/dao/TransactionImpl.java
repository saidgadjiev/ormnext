package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.stament_executor.IStatementExecutor;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class TransactionImpl<T, ID> implements Transaction<T, ID> {

    private final IStatementExecutor<T, ID> statementExecutor;

    private final Connection connection;

    private Callable<Void> close;

    private State state;

    public TransactionImpl(IStatementExecutor<T, ID> statementExecutor, Connection connection, Callable<Void> close) {
        this.statementExecutor = statementExecutor;
        this.connection = connection;
        this.close = close;
    }

    @Override
    public int create(Collection<T> objects) throws SQLException {
        check();
        return statementExecutor.create(connection, objects);
    }

    @Override
    public int create(T object) throws SQLException {
        check();
        return statementExecutor.create(connection, object);
    }

    @Override
    public boolean createTable(boolean ifNotExists) throws SQLException {
        check();
        return statementExecutor.createTable(connection, ifNotExists);
    }

    @Override
    public T queryForId(ID id) throws SQLException {
        check();
        return statementExecutor.queryForId(connection, id);
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        check();
        return statementExecutor.queryForAll(connection);
    }

    @Override
    public int update(T object) throws SQLException {
        check();
        return statementExecutor.update(connection, object);
    }

    @Override
    public int delete(T object) throws SQLException {
        check();
        return statementExecutor.delete(connection, object);
    }

    @Override
    public int deleteById(ID id) throws SQLException {
        check();
        return statementExecutor.deleteById(connection, id);
    }

    @Override
    public boolean dropTable(boolean ifExists) throws SQLException {
        check();
        return statementExecutor.dropTable(connection, ifExists);
    }

    @Override
    public void createIndexes() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void dropIndexes() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public long countOff() throws SQLException {
        return statementExecutor.countOff(connection);
    }

    @Override
    public <R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper) throws SQLException {
        check();
        return statementExecutor.query(connection, query, resultsMapper);
    }

    @Override
    public List<T> query(SelectStatement<T> statement) throws SQLException {
        check();
        return statementExecutor.query(connection, statement);
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
