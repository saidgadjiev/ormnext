package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.List;

public interface EntityLoader {

    /**
     * Create a new entry in the database from an object.
     *
     * @param connection target connection
     * @param object     target object
     * @throws SQLException any SQL exceptions
     */
    int create(DatabaseConnection connection, Object object) throws SQLException;

    /**
     * Create a new entries in the database from an object.
     *
     * @param connection target connection
     * @param objects    target objects
     * @throws SQLException any SQL exceptions
     */
    int create(DatabaseConnection connection, Object[] objects) throws SQLException;

    /**
     * Create table in the database.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param ifNotExist append if not exist
     * @return true if table create success
     * @throws SQLException any SQL exceptions
     */
    boolean createTable(DatabaseConnection connection, Class<?> tClass, boolean ifNotExist)
            throws SQLException;

    /**
     * Drop table from the database.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param ifExist    append if exist
     * @return true if table drop success
     * @throws SQLException any SQL exceptions
     */
    boolean dropTable(DatabaseConnection connection, Class<?> tClass, boolean ifExist)
                    throws SQLException;

    /**
     * Update an object in the database.
     *
     * @param connection target connection
     * @param object     target object
     * @throws SQLException any SQL exceptions
     */
    int update(DatabaseConnection connection, Object object) throws SQLException;

    /**
     * Delete an object from the database.
     *
     * @param connection target connection
     * @param object     target object
     * @throws SQLException any SQL exceptions
     */
    int delete(DatabaseConnection connection, Object object) throws SQLException;

    /**
     * Delete an object in the database by id.
     *
     * @param connection target connection
     * @param tClass     target object class
     * @param id         target id
     * @throws SQLException any SQL exceptions
     */
    int deleteById(DatabaseConnection connection, Class<?> tClass, Object id) throws SQLException;

    /**
     * Return the object associated with the id or null if none.
     *
     * @param <T>        result object type
     * @param connection target connection
     * @param tClass     result object class
     * @param id         target id
     * @return object associated with requested id
     * @throws SQLException any SQL exceptions
     */
    <T> T queryForId(DatabaseConnection connection, Class<T> tClass, Object id) throws SQLException;

    /**
     * Return all objects from table.
     *
     * @param connection target connection
     * @param tClass     result table class
     * @param <T>        table type
     * @return all objects from requested table
     * @throws SQLException any SQL exceptions
     */
    <T> List<T> queryForAll(DatabaseConnection connection, Class<T> tClass) throws SQLException;

    boolean refresh(DatabaseConnection databaseConnection, Object object) throws SQLException;

    /**
     * Create table indexes.
     *
     * @param connection target connection
     * @param tClass     table class
     * @throws SQLException any SQL exceptions
     */
    void createIndexes(DatabaseConnection connection, Class<?> tClass) throws SQLException;

    /**
     * Drop table indexes.
     *
     * @param connection target connection
     * @param tClass     table class
     * @throws SQLException any SQL exceptions
     */
    void dropIndexes(DatabaseConnection connection, Class<?> tClass) throws SQLException;

    /**
     * Count star in the table.
     *
     * @param connection target connection
     * @param tClass     table class
     * @return count off entities in the table
     * @throws SQLException any SQL exceptions
     */
    long countOff(DatabaseConnection connection, Class<?> tClass) throws SQLException;

    /**
     * Return all objects by select query.
     *
     * @param connection      target connection
     * @param selectStatement target query
     * @param <T>             table type
     * @return objects list
     * @throws SQLException any SQL exceptions
     */
    <T> List<T> list(DatabaseConnection connection, SelectStatement<T> selectStatement) throws SQLException;

    /**
     * Return long result by select query.
     *
     * @param connection      target connection
     * @param selectStatement target query
     * @return result long value
     * @throws SQLException any SQL exceptions
     */
    long queryForLong(DatabaseConnection connection, SelectStatement<?> selectStatement)
            throws SQLException;

    /**
     * Execute query by database engine and return results.
     *
     * @param databaseConnection target connection
     * @param query              target query
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(DatabaseConnection databaseConnection, String query) throws SQLException;

    default void batch() {
        throw new UnsupportedOperationException();
    }

    default int[] executeBatch(DatabaseConnection<?> databaseConnection) throws SQLException {
        throw new UnsupportedOperationException();
    }

    enum Loader {

        BATCH_LOADER,

        DEFAULT_LOADER
    }
}
