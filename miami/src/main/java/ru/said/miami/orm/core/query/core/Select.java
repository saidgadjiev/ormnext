package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.query.core.clause.GroupBy;
import ru.said.miami.orm.core.query.core.clause.Having;
import ru.said.miami.orm.core.query.core.clause.from.FromExpression;
import ru.said.miami.orm.core.query.core.clause.from.FromTable;
import ru.said.miami.orm.core.query.core.clause.select.SelectAll;
import ru.said.miami.orm.core.query.core.clause.select.SelectColumnsStrategy;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.condition.Equals;
import ru.said.miami.orm.core.query.core.condition.Expression;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

/**
 * Класс SELECT запроса
 */
public class Select implements QueryElement {

    private FromExpression from;

    private SelectionMode selectionMode;

    private SelectColumnsStrategy selectColumnsStrategy;

    private Expression where = new Expression();

    private GroupBy groupBy;

    private Having having;

    public FromExpression getFrom() {
        return from;
    }

    public void setFrom(FromExpression from) {
        this.from = from;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    public SelectColumnsStrategy getSelectColumnsStrategy() {
        return selectColumnsStrategy;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public void setSelectColumnsStrategy(SelectColumnsStrategy selectColumnsStrategy) {
        this.selectColumnsStrategy = selectColumnsStrategy;
    }

    public Having getHaving() {
        return having;
    }

    public void setHaving(Having having) {
        this.having = having;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public static <ID> Select buildQueryById(String typeName, DBFieldType idField, ID id) {
        Select selectQuery = new Select();

        selectQuery.setSelectColumnsStrategy(new SelectAll());
        selectQuery.setFrom(new FromTable(new TableRef(typeName)));
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(idField.getColumnName()).alias(new Alias(typeName)), idField.getDataPersister().getAssociatedOperand(id)));
        selectQuery.getWhere().getConditions().add(andCondition);

        return selectQuery;
    }

    public static Select buildQueryForAll(String typeName) {
        Select selectQuery = new Select();

        selectQuery.setFrom(new FromTable(new TableRef(typeName)));
        selectQuery.setSelectColumnsStrategy(new SelectAll());

        return selectQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        if (where != null) {
            where.accept(visitor);
        }
        if (groupBy != null) {
            groupBy.accept(visitor);
        }
        if (having != null) {
            having.accept(visitor);
        }
        visitor.finish(this);
    }

    enum SelectionMode {
        DISTINCT
    }

}
