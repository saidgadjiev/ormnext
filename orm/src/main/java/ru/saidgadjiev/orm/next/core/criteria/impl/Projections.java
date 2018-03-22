package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumn;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.condition.OperandCondition;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.core.function.CountExpression;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.core.function.SUM;

public class Projections {

    private Projections() {
    }

    public static ProjectionList projectionList() {
        return new ProjectionList();
    }

    public static DisplayedColumn selectColumn(String columnName, String alias) {
        DisplayedColumn displayedColumn = new DisplayedColumn(new ColumnSpec(columnName));

        if (alias != null && !alias.isEmpty()) {
            displayedColumn.setAlias(new Alias(alias));
        }

        return displayedColumn;
    }

    public static DisplayedOperand selectFunction(Function function, String alias) {
        DisplayedOperand displayedOperand = new DisplayedOperand(function);

        if (alias != null && !alias.isEmpty()) {
            displayedOperand.setAlias(new Alias(alias));
        }

        return displayedOperand;
    }

    public static Function sum(String columnName) {
        Expression expression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(new OperandCondition(new ColumnSpec(columnName)));
        expression.add(andCondition);

        return new SUM(expression);
    }

    public static Function countStar() {
        return new CountAll();
    }

    public static Function countOff(String columnName) {
        Expression expression = new Expression();
        AndCondition andCondition = new AndCondition();

        andCondition.add(new OperandCondition(new ColumnSpec(columnName)));
        expression.add(andCondition);

        return new CountExpression(expression);
    }


}
