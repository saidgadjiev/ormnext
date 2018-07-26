package ru.saidgadjiev.ormnext.core.query.criteria.impl;

public class Query {

    private final String query;

    private boolean cacheable = false;

    public Query(String query) {
        this.query = query;
    }

    public Query cacheable(boolean cacheable) {
        this.cacheable = cacheable;

        return this;
    }

    public String getQuery() {
        return query;
    }

    public boolean isCacheable() {
        return cacheable;
    }
}
