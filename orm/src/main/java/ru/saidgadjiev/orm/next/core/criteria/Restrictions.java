package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.GreaterThan;
import ru.saidgadjiev.orm.next.core.query.core.condition.InSelect;
import ru.saidgadjiev.orm.next.core.query.core.function.Function;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Restrictions {

    private Restrictions() {

    }

    public static Criterion eq(String columnName, Object value) {
        return new Criterion(new Equals(new ColumnSpec(columnName), new Param()), Arrays.asList(value));
    }

    public static Criterion ge(String columnName, Object value) {
        return new Criterion(new GreaterThan(new ColumnSpec(columnName), new Param()), Arrays.asList(value));
    }

    public static Criterion eq(Function function, Object value) {
        return new Criterion(new Equals(function, new Param()), Arrays.asList(value));
    }

    public static Criterion in(String columnName, SelectCriteria select) {
        return new Criterion(new InSelect(select.prepareSelect(), new ColumnSpec(columnName)), select.collectArgs().values());
    }
}
