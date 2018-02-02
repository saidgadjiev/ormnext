package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

/**
 * Класс DELETE запроса
 */
public class DeleteQuery implements QueryElement {

    private Expression where = new Expression();

    private String typeName;

    private DeleteQuery(String typeName) {
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

    public static <T> DeleteQuery buildQuery(String typeName, String idColumnName) {
        DeleteQuery deleteQuery = new DeleteQuery(typeName);
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(idColumnName).alias(new Alias(typeName)), new Param()));
        deleteQuery.getWhere().getConditions().add(andCondition);

        return deleteQuery;
    }

    public static DeleteQuery newQuery(String typeName) {
        return new DeleteQuery(typeName);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        where.accept(visitor);
        visitor.finish(this);
    }
}
