package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The definition of the Database Access Objects that handle the reading and writing a class from a database.
 *
 * @author Said Gadjiev
 */
public interface Dao {

    /**
     * Create a new row in the database from an object.
     *
     * @param object object that we are creating in the database
     * @return created count. This method return 1 if object created.
     * @throws SQLException on any SQL problems
     */
    int create(Object object) throws SQLException;

    /**
     * Create new rows in the database from an objects.
     *
     * @param objects objects that we are creating in the database
     * @return created objects count
     * @throws SQLException on any SQL problems
     */
    int create(Object[] objects) throws SQLException;

    /**
     * Create table in the database.
     *
     * @param entityClass     target table class
     * @param ifNotExist append if not exist part if true
     * @return true if table created or false
     * @throws SQLException on any SQL problems
     */
    boolean createTable(Class<?> entityClass, boolean ifNotExist) throws SQLException;

    /**
     * Create tables in the database.
     *
     * @param entityClasses    target tables classes
     * @param ifNotExist append if not exist part if true
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> createTables(Class<?>[] entityClasses, boolean ifNotExist) throws SQLException;

    /**
     * Drop requested table from the database.
     *
     * @param entityClass  target table class
     * @param ifExist append if exist part if true
     * @return true if drop is success or false
     * @throws SQLException on any SQL problems
     */
    boolean dropTable(Class<?> entityClass, boolean ifExist) throws SQLException;

    /**
     * Drop requested tables from the database.
     *
     * @param classes target tables classes
     * @param ifExist append if exist part if true
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> dropTables(Class<?>[] classes, boolean ifExist) throws SQLException;

    /**
     * Clear database table.
     *
     * @param entityClass target table class
     * @return deleted row count
     * @throws SQLException any SQL exceptions
     */
    int clearTable(Class<?> entityClass) throws SQLException;

    /**
     * Clear database tables.
     *
     * @param entityClasses target table classes
     * @return deleted row count
     * @throws SQLException any SQL exceptions
     */
    int clearTables(Class<?>[] entityClasses) throws SQLException;

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
     * @return updated count
     * @throws SQLException on any SQL problems
     */
    int update(Object object) throws SQLException;

    /**
     * Remove a database row associated with id from requested object.
     *
     * @param object target object
     * @return deleted count
     * @throws SQLException on any SQL problems
     */
    int delete(Object object) throws SQLException;

    /**
     * Remove a database row associated with requested id from requested table.
     *
     * @param tClass target table class
     * @param id     id
     * @return deleted count
     * @throws SQLException on any SQL problems
     */
    int deleteById(Class<?> tClass, Object id) throws SQLException;

    /**
     * Refresh requested object.
     *
     * @param object target object
     * @return true if refresh success
     * @throws SQLException any SQL exceptions
     */
    boolean refresh(Object object) throws SQLException;

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

    /**
     * Start batch execute.
     */
    void batch();

    /**
     * Execute batch. This should be called after call {@link #batch}.
     *
     * @return batch results
     * @throws SQLException any SQL exceptions
     */
    int[] executeBatch() throws SQLException;
}
