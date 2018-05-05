package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.ormnext.core.dao.transaction.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.InternalTransaction;
import ru.saidgadjiev.ormnext.core.dao.transaction.TransactionState;
import ru.saidgadjiev.ormnext.core.stamentexecutor.DefaultEntityLoader;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.dao.transaction.BeginState;
import ru.saidgadjiev.ormnext.core.dao.transaction.InternalTransaction;
import ru.saidgadjiev.ormnext.core.dao.transaction.TransactionState;
import ru.saidgadjiev.ormnext.core.stamentexecutor.DefaultEntityLoader;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class TransactionImpl implements InternalTransaction {

    private final DefaultEntityLoader statementExecutor;

    private final DatabaseConnection connection;

    private ConnectionSource connectionSource;

    private TransactionState transactionState = new BeginState(this);

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
    public <T> List<T> list(CriteriaQuery<T> criteriaQuery) throws SQLException {
        check();
        return statementExecutor.list(connection, criteriaQuery);
    }

    @Override
    public long queryForLong(SimpleCriteriaQuery simpleCriteriaQuery) throws SQLException {
        check();
        return statementExecutor.queryForLong(connection, simpleCriteriaQuery);
    }

    @Override
    public void begin() throws SQLException {
        transactionState.begin(connection);
    }

    @Override
    public void commit() throws SQLException {
        transactionState.commit(connection);
    }

    @Override
    public void rollback() throws SQLException {
        transactionState.rollback(connection);
    }

    private void check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is invalid");
        }
    }

    @Override
    public void changeState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    @Override
    public void releaseConnection(DatabaseConnection<?> connection) throws SQLException {
        connectionSource.releaseConnection(connection);
    }
}
