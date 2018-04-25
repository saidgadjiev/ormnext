package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.criteria.api.Criterion;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.*;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;

public class Restrictions {

    private Restrictions() {

    }

    public static Criterion eq(String propertyName) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Equals(new ColumnSpec(propertyName), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    public static Criterion eq(String propertyName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Equals(new ColumnSpec(propertyName), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion eq(Function function, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Equals(function, new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion ge(String propertyName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new GreaterThanOrEquals(new ColumnSpec(propertyName), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion ge(Function function, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new GreaterThanOrEquals(function, new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion gt(String propertyName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new GreaterThan(new ColumnSpec(propertyName), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion gt(Function function, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new GreaterThan(function, new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion le(String propertyName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new LessThanOrEquals(new ColumnSpec(propertyName), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion le(Function function, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new LessThanOrEquals(function, new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion lt(String propertyName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new LessThan(new ColumnSpec(propertyName), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion lt(Function function, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new LessThan(function, new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {value};
            }
        };
    }

    public static Criterion isNull(String propertyName) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new IsNull(new ColumnSpec(propertyName));
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
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
            public Object[] getArgs() {
                return new Object[] {};
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
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    public static Criterion between(String propertyName, Object low, Object high) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Between(new ColumnSpec(propertyName), new Param(), new Param());
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {low, high};
            }
        };
    }

    public static Criterion not(Condition condition) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Not(condition);
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    @SuppressWarnings("PMD")
    public static Criterion in(String propertyName, Object ... values) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                InValues inValues = new InValues(new ColumnSpec(propertyName));

                for (Object ignored : values) {
                    inValues.addValue(new Param());
                }

                return inValues;
            }

            @Override
            public Object[] getArgs() {
                return values;
            }
        };
    }

    @SuppressWarnings("PMD")
    public static Criterion notIn(String propertyName, Object ... values) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                NotInValues inValues = new NotInValues(new ColumnSpec(propertyName));

                for (Object ignored : values) {
                    inValues.addValue(new Param());
                }

                return inValues;
            }

            @Override
            public Object[] getArgs() {
                return values;
            }
        };
    }
}
