package ru.said.miami.orm.core.dao;

import ru.said.miami.orm.core.cache.LRUObjectCache;
import ru.said.miami.orm.core.cache.ObjectCache;
import ru.said.miami.orm.core.query.StatementExecutor;
import ru.said.miami.orm.core.query.core.Query;
import ru.said.miami.orm.core.query.core.object.DataBaseObject;
import ru.said.miami.orm.core.query.core.object.ObjectBuilder;
import ru.said.miami.orm.core.query.core.object.ObjectCreator;
import ru.said.miami.orm.core.query.core.query_builder.QueryBuilder;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Базовый класс для DAO. Используется в DaoBuilder
 * @param <T>
 * @param <ID>
 */
public abstract class BaseDaoImpl<T, ID> implements Dao<T, ID> {

    private final DataSource dataSource;

    private StatementExecutor<T, ID> statementExecutor;

    private DataBaseObject<T> dataBaseObject;

    private ObjectCache objectCache;

    public BaseDaoImpl(DataSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.dataBaseObject = new DataBaseObject<>(
                tableInfo,
                new ObjectCreator<>(dataSource, tableInfo),
                new ObjectBuilder<>(dataSource, tableInfo)
        );
        this.statementExecutor = new StatementExecutor<>(this.dataBaseObject);
    }



    @Override
    public int create(T object) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.create(connection, object);
        }
    }

    @Override
    public boolean createTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.createTable(connection);
        }
    }

    @Override
    public T queryForId(ID id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.queryForId(connection, id);
        }
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.queryForAll(connection);
        }
    }

    @Override
    public int update(T object) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.update(connection, object);
        }
    }

    @Override
    public int delete(T object) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.create(connection, object);
        }
    }

    @Override
    public int deleteById(ID id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.deleteById(connection, id);
        }
    }

    @Override
    public QueryBuilder<T> queryBuilder() {
        return new QueryBuilder<>(dataBaseObject);
    }

    @Override
    public <R> R query(Query<R> query) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return statementExecutor.execute(query, connection);
        }
    }

    @Override
    public void enableCache() {
        objectCache = new LRUObjectCache(32);
    }




    public static<T, ID> Dao<T, ID> createDao(DataSource dataSource, TableInfo<T> tableInfo) {
        return new BaseDaoImpl<T, ID>(dataSource, tableInfo) {};
    }
}
