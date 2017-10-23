package ru.said.miami.orm.core.query.stamentExecutor;

import ru.said.miami.orm.core.query.core.Query;
import ru.said.miami.orm.core.query.core.object.DataBaseObject;

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
    public boolean createTable(Connection connection) throws SQLException {
        return delegate.createTable(connection);
    }

    @Override
    public boolean dropTable(Connection connection) throws SQLException {
        return delegate.dropTable(connection);
    }

    @Override
    public int update(Connection connection, T object) throws SQLException {
        if (!dataBaseObject.getTableInfo().getIdField().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.update(connection, object);
    }

    @Override
    public int delete(Connection connection, T object) throws SQLException {
        if (!dataBaseObject.getTableInfo().getIdField().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.delete(connection, object);
    }

    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        if (!dataBaseObject.getTableInfo().getIdField().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.deleteById(connection, id);
    }

    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (!dataBaseObject.getTableInfo().getIdField().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.queryForId(connection, id);
    }

    @Override
    public List<T> queryForAll(Connection connection) throws SQLException {
        return delegate.queryForAll(connection);
    }

    @Override
    public <R> R execute(Query<R> query, Connection connection) throws SQLException {
        return delegate.execute(query, connection);
    }
}
