package ru.saidgadjiev.ormnext.core.query.criteria.impl;

/**
 * Query wrapper.
 *
 * @author Said Gadjiev
 */
public class Query {

    /**
     * Sql query.
     */
    private final String query;

    /**
     * Create a new instance.
     *
     * @param query target query
     */
    public Query(String query) {
        this.query = query;
    }

    /**
     * Retrieve query.
     *
     * @return query
     */
    public String getQuery() {
        return query;
    }

}
