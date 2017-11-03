package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;

public class DropIndexQuery implements Query<Boolean>,QueryElement {

    @Override
    public void accept(QueryVisitor visitor) {

    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        return null;
    }
}
