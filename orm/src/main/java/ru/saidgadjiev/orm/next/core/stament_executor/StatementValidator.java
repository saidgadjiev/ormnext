package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StatementValidator implements IStatementExecutor {

    private IStatementExecutor delegate;

    private TableInfo<?> tableInfo;

    public StatementValidator(TableInfo<?> tableInfo, IStatementExecutor delegate) {
        this.tableInfo = tableInfo;
        this.delegate = delegate;
    }

    @Override
    public<T> int create(Connection connection, Collection<T> objects) throws SQLException {
        return delegate.create(connection, objects);
    }

    @Override
    public<T> int create(Connection connection, T object) throws SQLException {
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
    public<T> int update(Connection connection, T object) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.update(connection, object);
    }

    @Override
    public<T> int delete(Connection connection, T object) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.delete(connection, object);
    }

    @Override
    public<ID> int deleteById(Connection connection, ID id) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.deleteById(connection, id);
    }

    @Override
    public<T, ID> T queryForId(Connection connection, ID id) throws SQLException {
        if (!tableInfo.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.queryForId(connection, id);
    }

    @Override
    public<T> List<T> queryForAll(Connection connection) throws SQLException {
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
