package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface IStatementExecutor {

    <T> int create(Connection connection, Collection<T> objects, Class<T> tClass) throws SQLException;

    @SuppressWarnings("unchecked")
    <T> int create(Connection connection, T object) throws SQLException;

    <T> boolean createTable(Connection connection, Class<T> tClass, boolean ifNotExists) throws SQLException;

    <T> boolean dropTable(Connection connection, Class<T> tClass, boolean ifExists) throws SQLException;

    <T> int update(Connection connection, T object) throws SQLException;

    <T> int delete(Connection connection, T object) throws SQLException;

    <T, ID> int deleteById(Connection connection, Class<T> tClass, ID id) throws SQLException;

    <T, ID> T queryForId(Connection connection, Class<T> tClass, ID id) throws SQLException;

    <T> List<T> queryForAll(Connection connection, Class<T> tClass) throws SQLException;

    <T> void createIndexes(Connection connection, Class<T> tClass) throws SQLException;

    <T> void dropIndexes(Connection connection, Class<T> tClass) throws SQLException;

    <R> GenericResults<R> query(Connection connection, String query, ResultsMapper<R> resultsMapper) throws SQLException;

    long queryForLong(String query, Connection connection) throws SQLException;

    <T> long countOff(Connection connection, Class<T> tClass) throws SQLException;
}
