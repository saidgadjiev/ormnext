package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс SELECT запроса
 */
public class SelectQuery implements Query, QueryElement {

    private final QueryVisitor visitor;

    private List<TableRef> from = new ArrayList<>();

    private Expression where = new Expression();

    private SelectQuery(QueryVisitor visitor) {
        this.visitor = visitor;
    }

    public List<TableRef> getFrom() {
        return from;
    }

    public Expression getWhere() {
        return where;
    }

    @Override
    public <T> T execute(Connection connection) throws SQLException {
        return null;
    }

    public static SelectQuery buildQuery() {
        return new SelectQuery(new DefaultVisitor());
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        for (TableRef tableRef: from) {
            tableRef.accept(visitor);
        }
        where.accept(visitor);
        visitor.finish(this);
    }
}
