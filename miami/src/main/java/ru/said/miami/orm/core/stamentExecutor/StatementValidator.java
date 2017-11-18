package ru.said.miami.orm.core.stamentExecutor;

import ru.said.miami.orm.core.stamentExecutor.object.DataBaseObject;
import ru.said.miami.orm.core.queryBuilder.PreparedQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StatementValidator<T, ID> implements IStatementExecutor<T, ID> {

    private IStatementExecutor<T, ID> delegate;

    private DataBaseObject<T> dataBaseObject;

    public StatementValidator(DataBaseObject<T> dataBaseObject) {
        this.dataBaseObject = dataBaseObject;
        delegate = new CachedStatementExecutor<>(dataBaseObject);
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
        if (!dataBaseObject.getTableInfo().getPrimaryKeys().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.update(connection, object);
    }

    @Override
    public int delete(Connection connection, T object) throws SQLException {
        if (!dataBaseObject.getTableInfo().getPrimaryKeys().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.delete(connection, object);
    }

    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        if (!dataBaseObject.getTableInfo().getPrimaryKeys().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.deleteById(connection, id);
    }

    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (!dataBaseObject.getTableInfo().getPrimaryKeys().isPresent()) {
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
    public List<T> query(PreparedQuery preparedQuery, Connection connection) throws SQLException {
        return delegate.query(preparedQuery, connection);
    }
}
