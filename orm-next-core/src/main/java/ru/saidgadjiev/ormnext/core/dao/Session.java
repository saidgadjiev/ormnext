package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.stament_executor.CacheHelper;

import java.sql.SQLException;

/**
 * This interface provide methods for work with transaction.
 */
public interface Session extends AutoCloseable, BaseDao {

    /**
     * Begin transaction.
     * @throws SQLException on any SQL problems
     */
    void beginTransaction() throws SQLException;

    /**
     * Commit transaction.
     * @throws SQLException on any SQL problems
     */
    void commit() throws SQLException;

    /**
     * Rollback transaction.
     * @throws SQLException on any SQL problems
     */
    void rollback() throws SQLException;

    /**
     * Cache helper.
     * @return current cache helper
     * @see CacheHelper
     */
    CacheHelper cacheHelper();

    /**
     * Return session manager {@link SessionManager} which open this session.
     * @return session manager which open this session
     */
    SessionManager getSessionManager();

    /**
     * Release all resources and close current database connection.
     * @throws SQLException on any SQL problems
     */
    void close() throws SQLException;
}
