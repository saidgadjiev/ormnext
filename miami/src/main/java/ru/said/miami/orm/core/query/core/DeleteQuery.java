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

    private Expression where = new Expression();

    private String typeName;

    public DeleteQuery(QueryVisitor visitor, String typeName) {
        this.visitor = visitor;
        this.typeName = typeName;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public <T> T execute(Connection connection) throws SQLException {
        return null;
    }

    public static DeleteQuery buildQuery(String typeName) {
        return new DeleteQuery(new DefaultVisitor(), typeName);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        where.accept(visitor);
        visitor.finish(this);
    }
}
