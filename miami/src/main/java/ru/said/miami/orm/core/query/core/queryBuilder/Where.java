package ru.said.miami.orm.core.query.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.sqlQuery.AndCondition;
import ru.said.miami.orm.core.query.core.ColumnSpec;
import ru.said.miami.orm.core.query.core.sqlQuery.Operand;
import ru.said.miami.orm.core.query.core.conditions.Condition;
import ru.said.miami.orm.core.query.core.conditions.Equals;
import ru.said.miami.orm.core.query.core.conditions.Expression;
import ru.said.miami.orm.core.query.core.literals.Param;

import java.sql.SQLException;

/**
 * Created by said on 14.10.17.
 */
public class Where<T> {

    private final QueryBuilder<T> preparedQuery;

    private final Expression where;

    private AndCondition currentAndCondition;

    private Condition currentCondition;

    public Where(QueryBuilder<T> preparedQuery, Expression where) {
        this.preparedQuery = preparedQuery;
        this.where = where;
    }

    public Where<T> eq(String name) {
        currentCondition = new Equals(new ColumnSpec(name), new Param());

        return this;
    }

    public Where<T> eq(String name, Object value) {
        Operand operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        currentCondition = new Equals(new ColumnSpec(name), operand);

        return this;
    }

    public Where and() {
        currentAndCondition.add(currentCondition);
        currentCondition = null;

        return this;
    }

    public Where or() {
        where.add(currentAndCondition);
        currentAndCondition = new AndCondition();

        return this;
    }

    private void checkCurrentCondition() {
        if (currentCondition != null) {
            currentAndCondition.add(currentCondition);
            where.add(currentAndCondition);
        }
    }

    public PreparedQuery prepare() throws SQLException {
        checkCurrentCondition();

        return preparedQuery.prepare();
    }
}
