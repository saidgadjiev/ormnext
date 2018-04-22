package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.condition.OperandCondition;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.core.function.CountExpression;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.core.function.SUM;

public class Projections {

    private Projections() {
    }

    public static Function sum(String propertyName) {
        Expression expression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(new OperandCondition(new ColumnSpec(propertyName)));
        expression.add(andCondition);

        return new SUM(expression);
    }

    public static Function count(String propertyName) {
        Expression expression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(new OperandCondition(new ColumnSpec(propertyName)));
        expression.add(andCondition);

        return new CountExpression(expression);
    }
}
