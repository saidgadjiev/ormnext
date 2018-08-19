package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.SessionCriteriaContract;

import java.sql.SQLException;

/**
 * Query wrapper.
 *
 * @author Said Gadjiev
 */
public class Query {

    /**
     * Sql executeQuery.
     */
    private final String query;

    /**
     * Session.
     */
    private SessionCriteriaContract session;

    /**
     * Create a new instance.
     *
     * @param query   target executeQuery
     * @param session target session
     */
    public Query(String query, SessionCriteriaContract session) {
        this.query = query;
        this.session = session;
    }

    /**
     * Retrieve executeQuery.
     *
     * @return executeQuery
     */
    public String getQuery() {
        return query;
    }

    /**
     * Execute query results.
     *
     * @return query results
     * @throws SQLException any SQL exceptions
     */
    public DatabaseResults executeQuery() throws SQLException {
        return session.executeQuery(this);
    }

    /**
     * Execute query {@link java.sql.Statement#executeUpdate(String)}.
     *
     * @return changed row count
     * @throws SQLException any SQL exceptions
     */
    public int executeUpdate() throws SQLException {
        return session.executeUpdate(this);
    }
}
