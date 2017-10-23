package ru.said.miami.orm.core.query.stamentExecutor;

import ru.said.miami.orm.core.cache.ObjectCache;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.Query;
import ru.said.miami.orm.core.query.core.object.DataBaseObject;
import ru.said.miami.orm.core.table.TableInfo;

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
    public boolean createTable(Connection connection) throws SQLException {
        return delegate.createTable(connection);
    }

    @Override
    public boolean dropTable(Connection connection) throws SQLException {
        return delegate.dropTable(connection);
    }

    @Override
    public int update(Connection connection, T object) throws SQLException {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();
        DBFieldType idFieldType = tableInfo.getIdField().get();

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
        DBFieldType dbFieldType = tableInfo.getIdField().get();
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
    public <R> R execute(Query<R> query, Connection connection) throws SQLException {
        return delegate.execute(query, connection);
    }
}
