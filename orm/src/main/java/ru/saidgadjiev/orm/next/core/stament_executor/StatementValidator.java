package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StatementValidator<T, ID> implements IStatementExecutor<T, ID> {

    private IStatementExecutor<T, ID> delegate;

    private TableInfo<T> tableInfo;

    public StatementValidator(TableInfo<T> tableInfo, IStatementExecutor<T, ID> delegate) {
        this.tableInfo = tableInfo;
        this.delegate = delegate;
    }

    @Override
    public int create(Connection connection, Collection<T> objects) throws SQLException {
        return delegate.create(connection, objects);
    }

    @Override
    public int create(Connection connection, T object) throws SQLException {
        return delegate.create(connection, object);
    }

    @Override
    public boolean createTable(Connection connection, boolean ifNotExists) throws SQLException {
        return delegate.createTable(connection, ifNotExists);
    }

    @Override
    public boolean dropTable(Connection connection, boolean ifExists) throws SQLException {
        return delegate.dropTable(connection, ifExists);
    }

    @Override
    public int update(Connection connection, T object) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.update(connection, object);
    }

    @Override
    public int delete(Connection connection, T object) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.delete(connection, object);
    }

    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.deleteById(connection, id);
    }

    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.queryForId(connection, id);
    }

    @Override
    public List<T> queryForAll(Connection connection) throws SQLException {
        return delegate.queryForAll(connection);
    }

    @Override
    public void createIndexes(Connection connection) throws SQLException {
        delegate.createIndexes(connection);
    }

    @Override
    public void dropIndexes(Connection connection) throws SQLException {
        delegate.dropIndexes(connection);
    }

    @Override
    public <R> GenericResults<R> query(ConnectionSource connectionSource, String query, Map<Integer, Object> args) throws SQLException {
        return delegate.query(connectionSource, query, args);
    }

    @Override
    public long queryForLong(Connection connection, String query) throws SQLException {
        return delegate.queryForLong(connection, query);
    }

    @Override
    public long countOff(Connection connection) throws SQLException {
        return delegate.countOff(connection);
    }

    @Override
    public<R> GenericResults<R> query(ConnectionSource connectionSource, SelectStatement<R> statement) throws SQLException {
        return delegate.query(connectionSource, statement);
    }
}
