package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Класс DELETE запроса
 */
public class DeleteQuery implements QueryElement {

    private Expression where = new Expression();

    private String typeName;

    public DeleteQuery(String typeName) {
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

    public static DeleteQuery newQuery(String typeName) {
        return new DeleteQuery(typeName);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            where.accept(visitor);
        }
    }
}
