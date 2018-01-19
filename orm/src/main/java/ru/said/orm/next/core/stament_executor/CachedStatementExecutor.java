package ru.said.orm.next.core.stament_executor;

import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.table.TableInfo;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CachedStatementExecutor<T, ID> implements IStatementExecutor<T, ID> {

    private IStatementExecutor<T, ID> delegate;

    private DataBaseObject<T> dataBaseObject;

    CachedStatementExecutor(DataBaseObject<T> dataBaseObject) {
        this.dataBaseObject = dataBaseObject;
        delegate = new StatementExecutorImpl<>(dataBaseObject);
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
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType idFieldType = tableInfo.getPrimaryKeys().get();

        try {
            if (dataBaseObject.getObjectCache().isPresent()) {
                ObjectCache objectCache = dataBaseObject.getObjectCache().get();
                T cachedData = objectCache.get(idFieldType.access(object));

                dataBaseObject.copy(object, cachedData);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return delegate.update(connection, object);
    }

    @Override
    public int delete(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType dbFieldType = tableInfo.getPrimaryKeys().get();
        Integer result = delegate.delete(connection, object);

        try {
            if (dataBaseObject.getObjectCache().isPresent()) {
                dataBaseObject.getObjectCache().get().invalidate(dbFieldType.access(object));
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        Integer result = delegate.deleteById(connection, id);

        if (dataBaseObject.getObjectCache().isPresent()) {
            dataBaseObject.getObjectCache().get().invalidate(id);
        }

        return result;
    }

    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        if (dataBaseObject.getObjectCache().isPresent()) {
            ObjectCache objectCache = dataBaseObject.getObjectCache().get();

            if (objectCache.contains(id)) {
                return objectCache.get(id);
            }
        }

        T object = delegate.queryForId(connection, id);

        //TODO:Двойная проверка
        if (dataBaseObject.getObjectCache().isPresent()) {
            dataBaseObject.getObjectCache().get().put(id, object);
        }

        return object;
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
    public <R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper, Connection connection) throws SQLException {
        return delegate.query(query, resultsMapper, connection);
    }

    @Override
    public long query(String query, Connection connection) throws SQLException {
        return delegate.query(query, connection);
    }
}
