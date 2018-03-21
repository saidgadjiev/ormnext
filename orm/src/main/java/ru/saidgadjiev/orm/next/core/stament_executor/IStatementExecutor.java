package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IStatementExecutor<T, ID> {

    int create(Connection connection, Collection<T> objects) throws SQLException;

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

    <R> GenericResults<R> query(ConnectionSource connectionSource, String query, Map<Integer, Object> args) throws SQLException;

    long queryForLong(Connection connection, String query) throws SQLException;

    long countOff(Connection connection) throws SQLException;

    <R> GenericResults<R> query(ConnectionSource connectionSource, SelectStatement<R> statement) throws SQLException;
}
