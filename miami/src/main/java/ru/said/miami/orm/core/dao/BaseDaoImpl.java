package ru.said.miami.orm.core.dao;

import ru.said.miami.orm.core.query.StatementExecutor;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Базовый класс для DAO. Используется в DaoManager
 * @param <T>
 * @param <ID>
 */
public abstract class BaseDaoImpl<T, ID> implements Dao<T, ID> {

    private final DataSource dataSource;
    private final TableInfo<T> tableInfo;
    private StatementExecutor<T, ID> statementExecutor;

    public BaseDaoImpl(DataSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.tableInfo = tableInfo;
        this.statementExecutor = new StatementExecutor<>(tableInfo, this);
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
    public DataSource getDataSource() {
        return dataSource;
    }

    public TableInfo<T> getTableInfo() {
        return tableInfo;
    }

    public static<T, ID> Dao<T, ID> createDao(DataSource dataSource, TableInfo<T> tableInfo) {
        return new BaseDaoImpl<T, ID>(dataSource, tableInfo) {};
    }

}
