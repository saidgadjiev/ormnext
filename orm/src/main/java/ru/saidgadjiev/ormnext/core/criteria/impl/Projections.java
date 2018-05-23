package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.core.AndCondition;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.core.condition.OperandCondition;
import ru.saidgadjiev.ormnext.core.query.core.function.CountExpression;
import ru.saidgadjiev.ormnext.core.query.core.function.Function;
import ru.saidgadjiev.ormnext.core.query.core.function.SUM;

/**
 * This class has static method for create projections eg. aggregate functions.
 */
public final class Projections {

    /**
     * Can't be instantiated.
     */
    private Projections() { }

    /**
     * Create new SUM function by property name. It will be used in {@link Restrictions}
     * @param propertyName target property name
     * @return new SUM function
     */
    public static Function sum(String propertyName) {
        Expression expression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(new OperandCondition(new ColumnSpec(propertyName)));
        expression.add(andCondition);

        return new SUM(expression);
    }

    /**
     * Create new COUNT function by property name.
     * @param propertyName target property name
     * @return new COUNT function
     */
    public static Function count(String propertyName) {
        Expression expression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(new OperandCondition(new ColumnSpec(propertyName)));
        expression.add(andCondition);

        return new CountExpression(expression);
    }
}
