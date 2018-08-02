package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * Implement this interface for create new entity loader.
 */
public interface EntityLoader {

    /**
     * Create a new entry in the database from an object.
     *
     * @param session target connection
     * @param object  target object
     * @return created count
     * @throws SQLException any SQL exceptions
     */
    int create(Session session, Object object) throws SQLException;

    /**
     * Create a new entries in the database from an object.
     *
     * @param session target connection
     * @param objects target objects
     * @return created count
     * @throws SQLException any SQL exceptions
     */
    int create(Session session, Object... objects) throws SQLException;

    /**
     * Create table in the database.
     *
     * @param session    target connection
     * @param tClass     table class
     * @param ifNotExist append if not exist
     * @return true if table create success
     * @throws SQLException any SQL exceptions
     */
    boolean createTable(Session session, Class<?> tClass, boolean ifNotExist) throws SQLException;

    /**
     * Drop table from the database.
     *
     * @param session target connection
     * @param tClass  table class
     * @param ifExist append if exist
     * @return true if table drop success
     * @throws SQLException any SQL exceptions
     */
    boolean dropTable(Session session, Class<?> tClass, boolean ifExist) throws SQLException;

    /**
     * Update an object in the database.
     *
     * @param session target connection
     * @param object  target object
     * @return updated count
     * @throws SQLException any SQL exceptions
     */
    int update(Session session, Object object) throws SQLException;

    /**
     * Delete an object from the database.
     *
     * @param session target connection
     * @param object  target object
     * @return deleted count
     * @throws SQLException any SQL exceptions
     */
    int delete(Session session, Object object) throws SQLException;

    /**
     * Delete an object in the database by id.
     *
     * @param session target session
     * @param tClass  target object class
     * @param id      target id
     * @return deleted count
     * @throws SQLException any SQL exceptions
     */
    int deleteById(Session session, Class<?> tClass, Object id) throws SQLException;

    /**
     * Return the object associated with the id or null if none.
     *
     * @param session target session
     * @param tClass  result object class
     * @param id      target id
     * @return object associated with requested id
     * @throws SQLException any SQL exceptions
     */
    Object queryForId(Session session, Class<?> tClass, Object id) throws SQLException;

    /**
     * Return all objects from table.
     *
     * @param session target session
     * @param tClass  result table class
     * @return all objects from requested table
     * @throws SQLException any SQL exceptions
     */
    List<Object> queryForAll(Session session, Class<?> tClass) throws SQLException;

    /**
     * Refresh requested object.
     *
     * @param session target session
     * @param object  target object
     * @return true if refresh success
     * @throws SQLException any SQL exceptions
     */
    boolean refresh(Session session, Object object) throws SQLException;

    /**
     * Create table indexes.
     *
     * @param session target session
     * @param tClass  table class
     * @throws SQLException any SQL exceptions
     */
    void createIndexes(Session session, Class<?> tClass) throws SQLException;

    /**
     * Drop table indexes.
     *
     * @param session target session
     * @param tClass  table class
     * @throws SQLException any SQL exceptions
     */
    void dropIndexes(Session session, Class<?> tClass) throws SQLException;

    /**
     * Count star in the table.
     *
     * @param session target session
     * @param tClass  table class
     * @return count off entities in the table
     * @throws SQLException any SQL exceptions
     */
    long countOff(Session session, Class<?> tClass) throws SQLException;

    /**
     * Return all objects by select query.
     *
     * @param session         target session
     * @param selectStatement target query
     * @return objects list
     * @throws SQLException any SQL exceptions
     */
    List<Object> list(Session session, SelectStatement<?> selectStatement) throws SQLException;

    /**
     * Return long result by select query.
     *
     * @param session         target session
     * @param selectStatement target query
     * @return result long value
     * @throws SQLException any SQL exceptions
     */
    long queryForLong(Session session, SelectStatement<?> selectStatement) throws SQLException;

    /**
     * Execute query by database engine and return results.
     *
     * @param session target session
     * @param query   target query
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(Session session, Query query) throws SQLException;

    /**
     * Clear table.
     *
     * @param session     target session
     * @param entityClass target entity class
     * @return deleted count
     * @throws SQLException any SQL exceptions
     */
    int clearTable(Session session, Class<?> entityClass) throws SQLException;

    /**
     * Create or update object.
     *
     * @param session target session
     * @param object  target object
     * @return create or update status
     * @throws SQLException any SQL exceptions
     */
    Dao.CreateOrUpdateStatus createOrUpdate(Session session, Object object) throws SQLException;

    /**
     * Check exist id in the database table.
     *
     * @param session     target session
     * @param entityClass table class
     * @param id          target id
     * @return true if row with id exist
     * @throws SQLException any SQL exceptions
     */
    boolean exist(Session session, Class<?> entityClass, Object id) throws SQLException;

    /**
     * Delete the database table rows by delete statement.
     *
     * @param session         target session
     * @param deleteStatement target statement
     * @return deleted rows count
     * @throws SQLException any SQL exceptions
     */
    int delete(Session session, DeleteStatement deleteStatement) throws SQLException;

    /**
     * Update the database table rows by update statement.
     *
     * @param session         target session
     * @param updateStatement target statement
     * @return updated rows count
     * @throws SQLException any SQL exceptions
     */
    int update(Session session, UpdateStatement updateStatement) throws SQLException;

    /**
     * Execute seelct statement by database engine and return results.
     *
     * @param session target session
     * @param selectStatement   target statement
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(Session session, SelectStatement<?> selectStatement) throws SQLException;

    /**
     * Entity loader types.
     */
    enum Loader {

        /**
         * Default entity loader.
         */
        DEFAULT_LOADER
    }
}
