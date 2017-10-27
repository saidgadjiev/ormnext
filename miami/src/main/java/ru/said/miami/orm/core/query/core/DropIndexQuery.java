package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;

public class DropIndexQuery<R> implements Query<R>,QueryElement {

    @Override
    public void accept(QueryVisitor visitor) {

    }

    @Override
    public R execute(Connection connection) throws SQLException {
        return null;
    }
}
