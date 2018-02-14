package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.GreaterThan;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;

public class Restrictions {

    private Restrictions() {

    }

    public static Criterion eq(String columnName, Object value) {
        return new Criterion(new Equals(new ColumnSpec(columnName), new Param()), value);
    }

    public static Criterion ge(String columnName, Object value) {
        return new Criterion(new GreaterThan(new ColumnSpec(columnName), new Param()), value);
    }

    public static Criterion eq(Function function, Object value) {
        return new Criterion(new Equals(function, new Param()), value);
    }
}
