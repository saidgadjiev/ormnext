package ru.saidgadjiev.ormnext.core.connection;

import java.sql.SQLException;

/**
 * Database connection.
 *
 * @param <T> connection type
 * @author Said Gadjiev
 */
public abstract class DatabaseConnection<T> {

    /**
     * Original connection.
     */
    private final T connection;

    /**
     * Create a new instance.
     *
     * @param connection target connection
     */
    public DatabaseConnection(T connection) {
        this.connection = connection;
    }

    /**
     * Return original connection {@link #connection}.
     *
     * @return connection
     */
    public final T getConnection() {
        return connection;
    }

    /**
     * Set connection auto commit.
     *
     * @param autoCommit true for disable auto commit
     * @throws SQLException any SQL exceptions
     */
    public abstract void setAutoCommit(boolean autoCommit) throws SQLException;

    /**
     * Commit.
     *
     * @throws SQLException any SQL exceptions
     */
    public abstract void commit() throws SQLException;

    /**
     * Rollback.
     *
     * @throws SQLException any SQL exceptions
     */
    public abstract void rollback() throws SQLException;

    /**
     * Check connection is closed.
     *
     * @return true if connection is closed
     * @throws SQLException any SQL exceptions
     */
    public abstract boolean isClosed() throws SQLException;

    /**
     * Close connection.
     *
     * @throws SQLException any SQL exceptions
     */
    public abstract void close() throws SQLException;
}
