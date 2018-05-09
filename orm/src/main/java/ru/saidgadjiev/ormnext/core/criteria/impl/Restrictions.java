package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.criteria.api.Criterion;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.condition.*;
import ru.saidgadjiev.ormnext.core.query.core.function.Function;
import ru.saidgadjiev.ormnext.core.query.core.literals.Param;

public class Restrictions {

    private Restrictions() {

    }

    public static Criterion eq(String propertyName) {
        return new CriterionImpl(new Equals(new ColumnSpec(propertyName), new Param()), propertyName, null);
    }

    public static Criterion eq(String propertyName, Object value) {
        return new CriterionImpl(new Equals(new ColumnSpec(propertyName), new Param()), propertyName, value);
    }

    public static Criterion eq(Function function, Object value) {
        return new CriterionImpl(new Equals(function, new Param()), null, value);
    }

    public static Criterion ge(String propertyName, Object value) {
        return new CriterionImpl(new GreaterThanOrEquals(new ColumnSpec(propertyName), new Param()), propertyName, value);
    }

    public static Criterion ge(Function function, Object value) {
        return new CriterionImpl(new GreaterThanOrEquals(function, new Param()), null, value);
    }

    public static Criterion gt(String propertyName, Object value) {
        return new CriterionImpl(new GreaterThan(new ColumnSpec(propertyName), new Param()), propertyName, value);
    }

    public static Criterion gt(Function function, Object value) {
        return new CriterionImpl(new GreaterThan(function, new Param()), null, value);
    }

    public static Criterion le(String propertyName, Object value) {
        return new CriterionImpl(new LessThanOrEquals(new ColumnSpec(propertyName), new Param()), propertyName, value);
    }

    public static Criterion le(Function function, Object value) {
        return new CriterionImpl(new LessThanOrEquals(function, new Param()), null, value);
    }

    public static Criterion lt(String propertyName, Object value) {
        return new CriterionImpl(new LessThan(new ColumnSpec(propertyName), new Param()), propertyName, value);
    }

    public static Criterion lt(Function function, Object value) {
        return new CriterionImpl(new LessThan(function, new Param()), null, value);
    }

    public static Criterion isNull(String propertyName) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new IsNull(new ColumnSpec(propertyName));
            }

            @Override
            public CriterionArgument getArg() {
                return null;
            }
        };
    }

    public static Criterion notNull(String propertyName) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new NotNull(new ColumnSpec(propertyName));
            }

            @Override
            public CriterionArgument getArg() {
                return null;
            }
        };
    }

    public static Criterion like(String propertyName, String pattern) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Like(new ColumnSpec(propertyName), pattern);
            }

            @Override
            public CriterionArgument getArg() {
                return null;
            }
        };
    }

    public static Criterion between(String propertyName, Object low, Object high) {
        return new CriterionImpl(new Between(new ColumnSpec(propertyName), new Param(), new Param()), null, low, high);
    }

    public static Criterion not(Condition condition) {
        return new CriterionImpl(new Not(condition), null, null);
    }

    @SuppressWarnings("PMD")
    public static Criterion in(String propertyName, Object... values) {
        InValues inValues = new InValues(new ColumnSpec(propertyName));

        for (Object ignored : values) {
            inValues.addValue(new Param());
        }

        return new CriterionImpl(inValues, propertyName, values);
    }

    @SuppressWarnings("PMD")
    public static Criterion notIn(String propertyName, Object... values) {
        NotInValues notInValues = new NotInValues(new ColumnSpec(propertyName));

        for (Object ignored : values) {
            notInValues.addValue(new Param());
        }

        return new CriterionImpl(notInValues, propertyName, values);
    }
}
