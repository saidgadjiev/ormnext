package ru.said.orm.next.core.stament_executor;

import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.field.field_type.IDBFieldType;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.table.TableInfo;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class CachedStatementExecutor<T, ID> implements IStatementExecutor<T, ID> {

    private IStatementExecutor<T, ID> delegate;

    private DataBaseObject<T> dataBaseObject;

    public CachedStatementExecutor(DataBaseObject<T> dataBaseObject, IStatementExecutor<T, ID> delegate) {
        this.dataBaseObject = dataBaseObject;
        this.delegate = delegate;
    }

    @Override
    public int create(Connection connection, T object) throws SQLException {
        Integer count = delegate.create(connection, object);

        if (count > 0 && dataBaseObject.isCaching() && dataBaseObject.getObjectCache().isPresent()) {
            ObjectCache objectCache = dataBaseObject.getObjectCache().get();
            TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

            if (tableInfo.getPrimaryKeys().isPresent()) {
                IDBFieldType idbFieldType = tableInfo.getPrimaryKeys().get();

                try {
                    objectCache.put(tableInfo.getTableClass(), idbFieldType.access(object), object);
                } catch (Exception ex) {
                    throw new SQLException(ex);
                }
            }
        }

        return count;
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
        Integer count = delegate.update(connection, object);

        if (count > 0) {
            TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
            IDBFieldType idFieldType = tableInfo.getPrimaryKeys().get();

            try {
                if (dataBaseObject.isCaching() && dataBaseObject.getObjectCache().isPresent()) {
                    ObjectCache objectCache = dataBaseObject.getObjectCache().get();
                    T cachedData = objectCache.get(tableInfo.getTableClass(), idFieldType.access(object));

                    if (cachedData != null) {
                        dataBaseObject.copy(object, cachedData);
                    }
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return count;
    }

    @Override
    public int delete(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        IDBFieldType dbFieldType = tableInfo.getPrimaryKeys().get();
        Integer result = delegate.delete(connection, object);

        try {
            Object id = dbFieldType.access(object);

            if (dataBaseObject.isCaching()) {
                dataBaseObject.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(tableInfo.getTableClass(), id));
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

        return result;
    }

    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        Integer result = delegate.deleteById(connection, id);
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        if (dataBaseObject.isCaching()) {
            dataBaseObject.getObjectCache().ifPresent(objectCache -> objectCache.invalidate(tableInfo.getTableClass(), id));
        }

        return result;
    }

    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        if (dataBaseObject.isCaching() && dataBaseObject.getObjectCache().isPresent()) {
            ObjectCache objectCache = dataBaseObject.getObjectCache().get();

            if (objectCache.contains(tableInfo.getTableClass(), id)) {
                return objectCache.get(tableInfo.getTableClass(), id);
            }
        }

        T object = delegate.queryForId(connection, id);

        if (object != null && dataBaseObject.isCaching()) {
            dataBaseObject.getObjectCache().ifPresent(objectCache -> objectCache.put(tableInfo.getTableClass(), id, object));
        }

        return object;
    }

    @Override
    public List<T> queryForAll(Connection connection) throws SQLException {

        List<T> result = delegate.queryForAll(connection);
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        try {
            if (tableInfo.getPrimaryKeys().isPresent() && dataBaseObject.isCaching() && dataBaseObject.getObjectCache().isPresent()) {
                IDBFieldType idbFieldType = tableInfo.getPrimaryKeys().get();
                ObjectCache objectCache = dataBaseObject.getObjectCache().get();

                for (T object : result) {
                    objectCache.put(tableInfo.getTableClass(), idbFieldType.access(object), object);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return result;
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
    public <R> GenericResults<R> query(Connection connection, String query, ResultsMapper<R> resultsMapper) throws SQLException {
        return delegate.query(connection, query, resultsMapper);
    }

    @Override
    public long query(String query, Connection connection) throws SQLException {
        return delegate.query(query, connection);
    }

    @Override
    public long countOff(Connection connection) throws SQLException {
        return delegate.countOff(connection);
    }
}
