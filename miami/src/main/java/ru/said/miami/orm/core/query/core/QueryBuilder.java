package ru.said.miami.orm.core.query.core;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryBuilder implements Query {

    @Override
    public <T> T execute(Connection connection) throws SQLException {
        return null;
    }
}
