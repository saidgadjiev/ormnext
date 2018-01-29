package ru.said.orm.next.core.dao;

import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.cache.ReferenceObjectCache;
import ru.said.orm.next.core.stament_executor.GenericResults;
import ru.said.orm.next.core.stament_executor.IStatementExecutor;
import ru.said.orm.next.core.stament_executor.ResultsMapper;
import ru.said.orm.next.core.stament_executor.StatementValidator;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.support.ConnectionSource;
import ru.said.orm.next.core.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Базовый класс для DAO. Используется в DaoBuilder
 *
 * @param <T>
 * @param <ID>
 */
public abstract class BaseDaoImpl<T, ID> implements Dao<T, ID> {

    private final ConnectionSource dataSource;

    private IStatementExecutor<T, ID> statementExecutor;

    private DataBaseObject<T> dataBaseObject;

    protected BaseDaoImpl(ConnectionSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.dataBaseObject = new DataBaseObject<>(
                dataSource,
                tableInfo
        );
        this.statementExecutor = new StatementValidator<>(this.dataBaseObject);
    }

    @Override
    public int create(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.create(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public boolean createTable(boolean ifNotExist) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.createTable(connection, ifNotExist);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public T queryForId(ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForId(connection, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.queryForAll(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int update(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.update(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int delete(T object) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.delete(connection, object);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public int deleteById(ID id) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.deleteById(connection, id);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void caching(boolean flag, ObjectCache objectCache) {
        if (flag) {
            if (objectCache != null) {
                dataBaseObject.setObjectCache(objectCache);
            } else {
                dataBaseObject.setObjectCache(new ReferenceObjectCache());
            }
        } else {
            dataBaseObject.setObjectCache(null);
        }
    }

    @Override
    public boolean dropTable(boolean ifExists) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.dropTable(connection, ifExists);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void createIndexes() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.createIndexes(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public void dropIndexes() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            statementExecutor.dropIndexes(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public long countOff() throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            return statementExecutor.countOff(connection);
        } finally {
            dataSource.releaseConnection(connection);
        }
    }

    @Override
    public <R> GenericResults<R> query(String query, ResultsMapper<R> resultsMapper) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.query(connection, query, resultsMapper);
        }
    }

    public static <T, ID> Dao<T, ID> createDao(ConnectionSource dataSource, TableInfo<T> tableInfoBuilder) {
        return new BaseDaoImpl<T, ID>(dataSource, tableInfoBuilder) {
        };
    }

    @Override
    public ConnectionSource getDataSource() {
        return dataSource;
    }

    @Override
    public TransactionImpl<T, ID> transaction() throws SQLException {
        Connection connection = dataSource.getConnection();

        return new TransactionImpl<>(statementExecutor, connection, () -> {
            dataSource.releaseConnection(connection);

            return null;
        });
    }
}
