package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.GreaterThan;
import ru.saidgadjiev.orm.next.core.query.core.condition.InSelect;
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

    public static Criterion ge(String columnName, Object value) {
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

    public static SubQueryCriterion in(String columnName, SelectCriteria select) {
        return new SubQueryCriterion() {
            @Override
            public Condition getCondition() {
                return new InSubQuery(select.getAlias(), new InSelect(select.prepareSelect(), new ColumnSpec(columnName)));
            }

            @Override
            public Object[] getArgs() {
                return select.collectArgs().toArray();
            }
        };
    }
}
