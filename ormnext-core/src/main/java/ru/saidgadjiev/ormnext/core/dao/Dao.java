package ru.saidgadjiev.ormnext.core.dao;

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
     * @param entityClass target table class
     * @param ifNotExist  append if not exist part if true
     * @return true if table created or false
     * @throws SQLException on any SQL problems
     */
    boolean createTable(Class<?> entityClass, boolean ifNotExist) throws SQLException;

    /**
     * Create tables in the database.
     *
     * @param entityClasses target tables classes
     * @param ifNotExist    append if not exist part if true
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> createTables(Class<?>[] entityClasses, boolean ifNotExist) throws SQLException;

    /**
     * Drop requested table from the database.
     *
     * @param entityClass target table class
     * @param ifExist     append if exist part if true
     * @return true if drop is success or false
     * @throws SQLException on any SQL problems
     */
    boolean dropTable(Class<?> entityClass, boolean ifExist) throws SQLException;

    /**
     * Drop requested tables from the database.
     *
     * @param entityClasses target tables classes
     * @param ifExist       append if exist part if true
     * @return table class, create status map
     * @throws SQLException on any SQL problems
     */
    Map<Class<?>, Boolean> dropTables(Class<?>[] entityClasses, boolean ifExist) throws SQLException;

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
     * Check exist object with id.
     *
     * @param entityClass target entity class
     * @param id          target object id
     * @return true if object exist
     * @throws SQLException any SQL exceptions
     */
    boolean exist(Class<?> entityClass, Object id) throws SQLException;

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
