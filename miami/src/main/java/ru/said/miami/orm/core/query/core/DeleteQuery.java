package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.FieldType;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
    public Integer execute(Connection connection) throws SQLException {
        this.accept(visitor);
        String sql = visitor.getQuery();

        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    public static <T> DeleteQuery buildQuery(String typeName, FieldType idField, T id) {
        DeleteQuery deleteQuery = new DeleteQuery(new DefaultVisitor(), typeName);
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(idField.getFieldName()), idField.getDataPersister().getAssociatedOperand(id)));
        deleteQuery.getWhere().getConditions().add(andCondition);

        return deleteQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        where.accept(visitor);
        visitor.finish(this);
    }
}
