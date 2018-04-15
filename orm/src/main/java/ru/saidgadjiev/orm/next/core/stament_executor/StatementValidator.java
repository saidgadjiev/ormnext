package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class StatementValidator implements IStatementExecutor {

    private IStatementExecutor delegate;

    public StatementValidator(IStatementExecutor delegate) {
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
    public<T> boolean createTable(Connection connection, Class<T> tClass, boolean ifNotExists) throws SQLException {
        return delegate.createTable(connection, tClass, ifNotExists);
    }

    @Override
    public<T> boolean dropTable(Connection connection, Class<T> tClass, boolean ifExists) throws SQLException {
        return delegate.dropTable(connection, tClass, ifExists);
    }

    @Override
    public<T> int update(Connection connection, T object) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());

        if (!databaseEntityMetadata.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.update(connection, object);
    }

    @Override
    public<T> int delete(Connection connection, T object) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<T>) object.getClass());

        if (!databaseEntityMetadata.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.delete(connection, object);
    }

    @Override
    public <T, ID> int deleteById(Connection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);

        if (!databaseEntityMetadata.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.deleteById(connection, tClass, id);
    }

    @Override
    public<T, ID> T queryForId(Connection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityMetadata<T> databaseEntityMetadata = TableInfoManager.buildOrGet(tClass);

        if (!databaseEntityMetadata.getPrimaryKey().isPresent()) {
            throw new SQLException("Id is not defined");
        }

        return delegate.queryForId(connection, tClass, id);
    }

    @Override
    public<T> List<T> queryForAll(Connection connection, Class<T> tClass) throws SQLException {
        return delegate.queryForAll(connection, tClass);
    }

    @Override
    public<T> void createIndexes(Connection connection, Class<T> tClass) throws SQLException {
        delegate.createIndexes(connection, tClass);
    }

    @Override
    public<T> void dropIndexes(Connection connection, Class<T> tClass) throws SQLException {
        delegate.dropIndexes(connection, tClass);
    }

    @Override
    public<T> long countOff(Connection connection, Class<T> tClass) throws SQLException {
        return delegate.countOff(connection, tClass);
    }

    @Override
    public<R> GenericResults<R> query(ConnectionSource connectionSource, SelectStatement<R> statement) throws SQLException {
        return delegate.query(connectionSource, statement);
    }
}
