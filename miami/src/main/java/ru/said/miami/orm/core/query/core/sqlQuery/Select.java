package ru.said.miami.orm.core.query.core.sqlQuery;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.query.core.ColumnSpec;
import ru.said.miami.orm.core.query.core.conditions.Equals;
import ru.said.miami.orm.core.query.core.conditions.Expression;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

/**
 * Класс SELECT запроса
 */
public class Select implements ISQLQuery {

    private TableRef from;

    private SelectExpression selectExpression;

    private Expression where = new Expression();

    public TableRef getFrom() {
        return from;
    }

    public void setFrom(TableRef from) {
        this.from = from;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    public SelectExpression getSelectExpression() {
        return selectExpression;
    }

    public static <ID> Select buildQueryById(String typeName, DBFieldType idField, ID id) {
        Select selectQuery = new Select();
        selectQuery.setFrom(new TableRef(typeName));
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(idField.getColumnName()), idField.getDataPersister().getAssociatedOperand(id)));
        selectQuery.getWhere().getConditions().add(andCondition);

        return selectQuery;
    }

    public static Select buildQueryForAll(String typeName) {
        Select selectQuery = new Select();
        selectQuery.setFrom(new TableRef(typeName));

        return selectQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        where.accept(visitor);
        visitor.finish(this);
    }
}
