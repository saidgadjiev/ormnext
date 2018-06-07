package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;

import java.sql.SQLException;

/**
 * This interface provide methods for work with transaction.
 *
 * @author Said Gadjiev
 */
public interface Session extends AutoCloseable, Dao {

    /**
     * Begin transaction.
     *
     * @throws SQLException on any SQL problems
     */
    void beginTransaction() throws SQLException;

    /**
     * Commit transaction.
     *
     * @throws SQLException on any SQL problems
     */
    void commit() throws SQLException;

    /**
     * Rollback transaction.
     *
     * @throws SQLException on any SQL problems
     */
    void rollback() throws SQLException;

    /**
     * Return session manager {@link SessionManager} which open this session.
     *
     * @return session manager which open this session
     */
    SessionManager getSessionManager();

    /**
     * Return current connection.
     *
     * @param <T> connection type
     * @return connection
     */
    <T> DatabaseConnection<T> getConnection();

    /**
     * Release all resources and close current database connection.
     *
     * @throws SQLException on any SQL problems
     */
    void close() throws SQLException;
}
