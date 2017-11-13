package ru.said.miami.orm.core.query.stamentExecutor;

import ru.said.miami.orm.core.query.core.queryBuilder.PreparedQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IStatementExecutor<T, ID> {
    @SuppressWarnings("unchecked")
    int create(Connection connection, T object) throws SQLException;

    boolean createTable(Connection connection, boolean ifNotExists) throws SQLException;

    boolean dropTable(Connection connection, boolean ifExists) throws SQLException;

    int update(Connection connection, T object) throws SQLException;

    int delete(Connection connection, T object) throws SQLException;

    int deleteById(Connection connection, ID id) throws SQLException;

    T queryForId(Connection connection, ID id) throws SQLException;

    List<T> queryForAll(Connection connection) throws SQLException;

    void createIndexes(Connection connection) throws SQLException;

    void dropIndexes(Connection connection) throws SQLException;

    List<T> query(PreparedQuery preparedQuery, Connection connection) throws SQLException;
}
