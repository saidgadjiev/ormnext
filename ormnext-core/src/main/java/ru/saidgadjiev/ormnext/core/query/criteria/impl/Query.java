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
    private SessionCriteriaContract session;

    /**
     * Create a new instance.
     *
     * @param query target executeQuery
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

    public DatabaseResults executeQuery() throws SQLException {
        return session.executeQuery(this);
    }

    public int executeUpdate() throws SQLException {
        return session.executeUpdate(this);
    }
}
