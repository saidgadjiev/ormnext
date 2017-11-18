package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.query.core.AndCondition;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.Select;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.condition.*;

public class BaseWhere {

    protected final Expression where;

    private AndCondition currentAndCondition = new AndCondition();

    private Condition currentCondition;

    protected BaseWhere(Expression where) {
        this.where = where;
    }

    protected void eq(Operand first, Operand second) {
        currentCondition = new Equals(first, second);
    }

    protected void operandCondition(Operand operand) {
        currentCondition = new OperandCondition(operand);
    }

    protected void gt(Operand first, Operand second) {
        currentCondition = new GreaterThan(first, second);
    }

    protected void ge(Operand first, Operand second) {
        currentCondition = new GreaterThanOrEquals(first, second);
    }

    protected void lt(Operand first, Operand second) {
        currentCondition = new LessThan(first, second);
    }

    protected void le(ColumnSpec columnSpec, Operand operand) {
        currentCondition = new LessThanOrEquals(columnSpec, operand);
    }

    protected void in(Select select, Operand operand) {
        currentCondition = new InSelect(select, operand);
    }

    protected void notIn(Select select, Operand operand) {
        currentCondition = new NotInSelect(select, operand);
    }

    protected void exists(Select select, Operand operand) {
        currentCondition = new Exists(select, operand);
    }

    protected void andClause() {
        currentAndCondition.add(currentCondition);
        currentCondition = null;
    }

    protected void orClause() {
        where.add(currentAndCondition);
        currentAndCondition = new AndCondition();
    }

    protected void checkCurrentCondition() {
        if (currentCondition != null) {
            currentAndCondition.add(currentCondition);
            where.add(currentAndCondition);
        }
    }
}
