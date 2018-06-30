package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.criteria.api.Criterion;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.Function;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.Param;

/**
 * This class provide static functions for create restrictions.
 *
 * @author Said Gadjiev
 */
public final class Restrictions {

    /**
     * Can't be instantiated.
     */
    private Restrictions() { }

    /**
     * Create equals restriction from entity property.
     * @param property target property
     * @param value target value
     * @return Criterion
     */
    public static Criterion eq(String property, Object value) {
        return new CriterionImpl(new Equals(new PropertyColumnSpec(property), new Param()), property, value);
    }

    /**
     * Create equals restriction from requested function {@link Function}. For example will be use in having.
     * @param function target function
     * @param value target value
     * @return Criterion
     */
    public static Criterion eq(Function function, Object value) {
        return new CriterionImpl(new Equals(function, new Param()), null, value);
    }

    /**
     * Create greater than or equals restriction from requested property.
     * @param property target property
     * @param value target value
     * @return Criterion
     */
    public static Criterion ge(String property, Object value) {
        return new CriterionImpl(
                new GreaterThanOrEquals(new PropertyColumnSpec(property), new Param()),
                property,
                value
        );
    }

    /**
     * Create greater than or equals restriction from requested function {@link Function}.
     * @param function target function
     * @param value target value
     * @return Criterion
     */
    public static Criterion ge(Function function, Object value) {
        return new CriterionImpl(new GreaterThanOrEquals(function, new Param()), null, value);
    }

    /**
     * Create greater than restriction from requested function property.
     * @param property target property
     * @param value target value
     * @return Criterion
     */
    public static Criterion gt(String property, Object value) {
        return new CriterionImpl(new GreaterThan(new PropertyColumnSpec(property), new Param()), property, value);
    }

    /**
     * Create greater than restriction from requested function {@link Function}.
     * @param function target function
     * @param value target value
     * @return Criterion
     */
    public static Criterion gt(Function function, Object value) {
        return new CriterionImpl(new GreaterThan(function, new Param()), null, value);
    }

    /**
     * Create less than or equals restriction from requested property.
     * @param property target property
     * @param value target value
     * @return Criterion
     */
    public static Criterion le(String property, Object value) {
        return new CriterionImpl(new LessThanOrEquals(new PropertyColumnSpec(property), new Param()), property, value);
    }

    /**
     * Create less than or equals restriction from requested function {@link Function}.
     * @param function target function
     * @param value target value
     * @return Criterion
     */
    public static Criterion le(Function function, Object value) {
        return new CriterionImpl(new LessThanOrEquals(function, new Param()), null, value);
    }
    /**
     * Create less than restriction from requested function {@link Function}.
     * @param property target property
     * @param value target value
     * @return Criterion
     */
    public static Criterion lt(String property, Object value) {
        return new CriterionImpl(new LessThan(new PropertyColumnSpec(property), new Param()), property, value);
    }
    /**
     * Create less than restriction from requested function {@link Function}.
     * @param function target function
     * @param value target value
     * @return Criterion
     */
    public static Criterion lt(Function function, Object value) {
        return new CriterionImpl(new LessThan(function, new Param()), null, value);
    }
    /**
     * Create "is null" restriction from requested property.
     * @param property target property
     * @return Criterion
     */
    public static Criterion isNull(String property) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new IsNull(new PropertyColumnSpec(property));
            }

            @Override
            public CriterionArgument getArg() {
                return null;
            }
        };
    }
    /**
     * Create "is not null" restriction from requested property.
     * @param property target property
     * @return Criterion
     */
    public static Criterion isNotNull(String property) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new NotNull(new PropertyColumnSpec(property));
            }

            @Override
            public CriterionArgument getArg() {
                return null;
            }
        };
    }
    /**
     * Create "like" restriction from requested property.
     * @param property target property
     * @param pattern target like pattern
     * @return Criterion
     */
    public static Criterion like(String property, String pattern) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Like(new PropertyColumnSpec(property), pattern);
            }

            @Override
            public CriterionArgument getArg() {
                return null;
            }
        };
    }
    /**
     * Create "between" restriction from requested property.
     * @param property target property
     * @param low lower bound
     * @param high higher bound
     * @return Criterion
     */
    public static Criterion between(String property, Object low, Object high) {
        return new CriterionImpl(
                new Between(new PropertyColumnSpec(property), new Param(), new Param()),
                property,
                low,
                high);
    }
    /**
     * Create "!" restriction from requested criterion {@link Criterion}.
     * @param criterion target criterion
     * @return Criterion
     */
    public static Criterion not(Criterion criterion) {
        return new CriterionImpl(new Not(criterion.getCondition()), null, null);
    }
    /**
     * Create "in" restriction from requested property.
     * @param property target property
     * @param values target values
     * @return Criterion
     */
    public static Criterion in(String property, Object... values) {
        InValues inValues = new InValues(new PropertyColumnSpec(property));

        for (int i = 0; i < values.length; ++i) {
            inValues.addValue(new Param());
        }

        return new CriterionImpl(inValues, property, values);
    }
    /**
     * Create "not in" restriction from property.
     * @param property target property
     * @param values target values
     * @return Criterion
     */
    public static Criterion notIn(String property, Object... values) {
        NotInValues notInValues = new NotInValues(new PropertyColumnSpec(property));

        for (int i = 0; i < values.length; ++i) {
            notInValues.addValue(new Param());
        }

        return new CriterionImpl(notInValues, property, values);
    }
}
