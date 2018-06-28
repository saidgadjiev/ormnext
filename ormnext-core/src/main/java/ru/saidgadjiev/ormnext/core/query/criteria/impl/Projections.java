package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.visitor.element.AndCondition;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.OperandCondition;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.CountExpression;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.Function;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.SUM;

/**
 * This class has static method for create projections eg. aggregate functions.
 *
 * @author Said Gadjiev
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

        andCondition.add(new OperandCondition(new PropertyColumnSpec(propertyName)));
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

        andCondition.add(new OperandCondition(new PropertyColumnSpec(propertyName)));
        expression.add(andCondition);

        return new CountExpression(expression);
    }
}
