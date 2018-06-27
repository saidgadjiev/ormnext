package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

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
     * Create or update object.
     *
     * @param object target object
     * @return create or update status
     * @throws SQLException any SQL exceptions
     * @see CreateOrUpdateStatus
     */
    CreateOrUpdateStatus createOrUpdate(Object object) throws SQLException;

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
    int create(Object... objects) throws SQLException;

    /**
     * Create table in the database.
     *
     * @param ifNotExist  append if not exist part if true
     * @param entityClass target table class
     * @return true if table created or false
     * @throws SQLException on any SQL problems
     */
    boolean createTable(boolean ifNotExist, Class<?> entityClass) throws SQLException;

    /**
     * Create tables in the database.
     *
     * @param ifNotExist    append if not exist part if true
     * @param entityClasses target tables classes
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> createTables(boolean ifNotExist, Class<?>... entityClasses) throws SQLException;

    /**
     * Drop requested table from the database.
     *
     * @param ifExist     append if exist part if true
     * @param entityClass target table class
     * @return true if drop is success or false
     * @throws SQLException on any SQL problems
     */
    boolean dropTable(boolean ifExist, Class<?> entityClass) throws SQLException;

    /**
     * Drop requested tables from the database.
     *
     * @param ifExist       append if exist part if true
     * @param entityClasses target tables classes
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> dropTables(boolean ifExist, Class<?>... entityClasses) throws SQLException;

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
    int clearTables(Class<?>... entityClasses) throws SQLException;

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

    /**
     * Return query result object.
     *
     * @param selectStatement target query statement
     * @param <T>             object type
     * @return query result object
     * @throws SQLException any SQL exceptions
     */
    <T> T uniqueResult(SelectStatement<T> selectStatement) throws SQLException;

    /**
     * Check exist object with id.
     *
     * @param entityClass target entity class
     * @param id          target object id
     * @return true if object exist
     * @throws SQLException any SQL exceptions
     */
    boolean exist(Class<?> entityClass, Object id) throws SQLException;

    /**
     * Delete the database table rows by delete statement.
     *
     * @param deleteStatement target statement
     * @return deleted rows count
     * @throws SQLException any SQL exceptions
     */
    int delete(DeleteStatement deleteStatement) throws SQLException;

    /**
     * Update the database table rows by update statement.
     *
     * @param updateStatement target statement
     * @return updated rows count
     * @throws SQLException any SQL exceptions
     */
    int update(UpdateStatement updateStatement) throws SQLException;

    DatabaseResults query(SelectStatement<?> selectStatement) throws SQLException;

    /**
     * Create or update object status.
     */
    final class CreateOrUpdateStatus {

        /**
         * True if an object was updated.
         */
        private final boolean updated;

        /**
         * True if an object was created.
         */
        private final boolean created;

        /**
         * Updated or created row count.
         */
        private final int rowCount;

        /**
         * Create a new instance.
         *
         * @param updated  true if an object was updated
         * @param created  true if an object was created
         * @param rowCount target updated or created row count
         */
        public CreateOrUpdateStatus(boolean updated, boolean created, int rowCount) {
            this.updated = updated;
            this.created = created;
            this.rowCount = rowCount;
        }

        /**
         * Is updated?
         *
         * @return is updated
         */
        public boolean isUpdated() {
            return updated;
        }

        /**
         * Is created?
         *
         * @return is updated
         */
        public boolean isCreated() {
            return created;
        }

        /**
         * Return updated or created row count.
         *
         * @return updated or created row count
         */
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public String toString() {
            return "CreateOrUpdateStatus{"
                    + "updated=" + updated
                    + ", created=" + created
                    + ", rowCount=" + rowCount
                    + '}';
        }
    }
}
