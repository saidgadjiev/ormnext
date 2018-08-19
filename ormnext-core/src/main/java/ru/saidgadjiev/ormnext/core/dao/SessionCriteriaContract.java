package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface represent contract between criteria api and session.
 *
 * @author Said Gadjiev
 */
public interface SessionCriteriaContract {

    /**
     * Return executeQuery result object.
     *
     * @param selectStatement target executeQuery statement
     * @param <T>             object type
     * @return executeQuery result object
     * @throws SQLException any SQL exceptions
     */
    <T> T uniqueResult(SelectStatement<T> selectStatement) throws SQLException;

    /**
     * Query for the items in the object table which match the select executeQuery.
     *
     * @param selectStatement target criteria executeQuery
     * @param <T>             object type
     * @return a list of all of the objects in the table that match the executeQuery.
     * @throws SQLException on any SQL problems
     * @see SelectStatement
     */
    <T> List<T> list(SelectStatement<T> selectStatement) throws SQLException;

    /**
     * Query for aggregate functions which retrieve one long value.
     *
     * @param selectStatement target executeQuery
     * @return long value which return executeQuery
     * @throws SQLException on any SQL problems
     * @see SelectStatement
     */
    long queryForLong(SelectStatement<?> selectStatement) throws SQLException;


    /**
     * Execute executeQuery statement and return results.
     *
     * @param selectStatement target statement
     * @return executeQuery results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(SelectStatement<?> selectStatement) throws SQLException;

    /**
     * Update the database table rows by update statement.
     *
     * @param updateStatement target statement
     * @return updated rows count
     * @throws SQLException any SQL exceptions
     */
    int update(UpdateStatement updateStatement) throws SQLException;

    /**
     * Delete the database table rows by delete statement.
     *
     * @param deleteStatement target statement
     * @return deleted rows count
     * @throws SQLException any SQL exceptions
     */
    int delete(DeleteStatement deleteStatement) throws SQLException;

    /**
     * Execute executeQuery and return results.
     *
     * @param query target executeQuery
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults executeQuery(Query query) throws SQLException;

    /**
     * Execute query {@link java.sql.Statement#executeUpdate(String)}.
     *
     * @param query target query
     * @return changed row count
     * @throws SQLException any SQL exceptions
     */
    int executeUpdate(Query query) throws SQLException;
}
