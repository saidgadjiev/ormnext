package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс DELETE запроса
 */
public class DeleteQuery implements Query, QueryElement {

    private final QueryVisitor visitor;

    public DeleteQuery(QueryVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public <T> T execute(Connection connection) throws SQLException {
        return null;
    }

    public static DeleteQuery buildQuery() {
        return new DeleteQuery(new DefaultVisitor());
    }

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
