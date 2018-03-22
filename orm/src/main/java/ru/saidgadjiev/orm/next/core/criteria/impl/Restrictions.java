package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.criteria.api.Criterion;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.*;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;

public class Restrictions {

    private Restrictions() {

    }

    public static Criterion eq(String columnName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Equals(new ColumnSpec(columnName), new Param());
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

    public static Criterion ge(String columnName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new GreaterThanOrEquals(new ColumnSpec(columnName), new Param());
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

    public static Criterion gt(String columnName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new GreaterThan(new ColumnSpec(columnName), new Param());
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

    public static Criterion le(String columnName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new LessThanOrEquals(new ColumnSpec(columnName), new Param());
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

    public static Criterion lt(String columnName, Object value) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new LessThan(new ColumnSpec(columnName), new Param());
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

    public static Criterion isNull(String columnName) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new IsNull(new ColumnSpec(columnName));
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    public static Criterion notNull(String columnName) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new NotNull(new ColumnSpec(columnName));
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    public static Criterion notEq(String column1, String column2) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new NotEquals(new ColumnSpec(column1), new ColumnSpec(column2));
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    public static Criterion like(String columnName, String pattern) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Like(new ColumnSpec(columnName), pattern);
            }

            @Override
            public Object[] getArgs() {
                return new Object[] {};
            }
        };
    }

    public static Criterion between(String columnName, Object low, Object high) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Between(new ColumnSpec(columnName), new Param(), new Param());
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

    public static Criterion exists(SelectStatement<?> select) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new Exists(select.prepareSelect());
            }

            @Override
            public Object[] getArgs() {
                return select.collectArgs().toArray();
            }
        };
    }

    public static Criterion in(String columnName, SelectStatement<?> select) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new InSelect(select.prepareSelect(), new ColumnSpec(columnName));
            }

            @Override
            public Object[] getArgs() {
                return select.collectArgs().toArray();
            }
        };
    }

    public static Criterion in(String columnName, Object ... values) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                InValues inValues = new InValues(new ColumnSpec(columnName));

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

    public static Criterion notIn(String columnName, SelectStatement<?> select) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                return new NotInSelect(select.prepareSelect(), new ColumnSpec(columnName));
            }

            @Override
            public Object[] getArgs() {
                return select.collectArgs().toArray();
            }
        };
    }

    public static Criterion notIn(String columnName, Object ... values) {
        return new Criterion() {
            @Override
            public Condition getCondition() {
                NotInValues inValues = new NotInValues(new ColumnSpec(columnName));

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
