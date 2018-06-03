package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The definition of the Database Access Objects that handle the reading and writing a class from a database.
 *
 * @author said gadjiev
 */
public interface Dao {

    /**
     * Create new row in the database from an object.
     *
     * @param object object that we are creating in the database
     * @throws SQLException on any SQL problems
     */
    int create(Object object) throws SQLException;

    int create(Object[] object) throws SQLException;
    /**
     * Create table in the database.
     *
     * @param tClass     target table class
     * @param ifNotExist append if not exist part if true
     * @return true if table created or false
     * @throws SQLException on any SQL problems
     */
    boolean createTable(Class<?> tClass, boolean ifNotExist) throws SQLException;

    /**
     * Create tables in the database.
     *
     * @param classes     target tables classes
     * @param ifNotExist append if not exist part if true
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> createTables(Class<?>[] classes, boolean ifNotExist) throws SQLException;

    /**
     * Retrieves an object associated with id.
     *
     * @param tClass target table class
     * @param id     identifier that matches a specific row in the database to find and return.
     * @param <T>    table class type
     * @return object that associated with requested id
     * @throws SQLException on any SQL problems
     */
    <T> T queryForId(Class<T> tClass, Object id) throws SQLException;

    /**
     * Retrieves all objects from database table.
     *
     * @param tClass target table class
     * @param <T>    table class type
     * @return A list of all of the objects in the table
     * @throws SQLException on any SQL problems
     */
    <T> List<T> queryForAll(Class<T> tClass) throws SQLException;

    /**
     * Update a database row associated with id from requested object.
     *
     * @param object target object
     * @throws SQLException on any SQL problems
     */
    int update(Object object) throws SQLException;

    /**
     * Remove a database row associated with id from requested object.
     *
     * @param object target object
     * @throws SQLException on any SQL problems
     */
    int delete(Object object) throws SQLException;

    /**
     * Remove a database row associated with requested id from requested table.
     *
     * @param tClass target table class
     * @param id     id
     * @throws SQLException on any SQL problems
     */
    int deleteById(Class<?> tClass, Object id) throws SQLException;

    boolean refresh(Object object) throws SQLException;

    /**
     * Drop requested table from the database.
     *
     * @param tClass  target table class
     * @param ifExist append if exist part if true
     * @return true if drop is success or false
     * @throws SQLException on any SQL problems
     */
    boolean dropTable(Class<?> tClass, boolean ifExist) throws SQLException;

    /**
     * Drop requested tables from the database.
     *
     * @param classes  target tables classes
     * @param ifExist append if exist part if true
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> dropTables(Class<?>[] classes, boolean ifExist) throws SQLException;

    /**
     * Create indexes by a requested table.
     *
     * @param tClass target table class
     * @throws SQLException on any SQL problems
     */
    void createIndexes(Class<?> tClass) throws SQLException;

    /**
     * Drop requested table indexes.
     *
     * @param tClass target table class
     * @throws SQLException on any SQL problems
     */
    void dropIndexes(Class<?> tClass) throws SQLException;

    /**
     * Count star in requested table.
     *
     * @param tClass target table class
     * @return entities count in requested table
     * @throws SQLException on any SQL problems
     */
    long countOff(Class<?> tClass) throws SQLException;

    /**
     * Query for the items in the object table which match the select query.
     *
     * @param selectStatement target criteria query
     * @param <T>             object type
     * @return a list of all of the objects in the table that match the query.
     * @throws SQLException on any SQL problems
     * @see SelectStatement
     */
    <T> List<T> list(SelectStatement<T> selectStatement) throws SQLException;

    /**
     * Query for aggregate functions which retrieve one long value.
     *
     * @param selectStatement target query
     * @return long value which return query
     * @throws SQLException on any SQL problems
     * @see SelectStatement
     */
    long queryForLong(SelectStatement<?> selectStatement) throws SQLException;

    /**
     * Execute query and return results.
     *
     * @param query target query
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(String query) throws SQLException;

    void batch();

    int[] executeBatch() throws SQLException;
}
