package ru.said.miami.orm.core.query.core.queryBuilder;

import ru.said.miami.orm.core.query.core.conditions.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 11.11.17.
 */
public class WhereObject<T> {

    private Expression where = new Expression();

    private List<IPreparedQuery<T>> preparedQueries = new ArrayList<>();

    public void addPrepared(IPreparedQuery<T> preparedQuery) {
        preparedQueries.add(preparedQuery);
    }

    public Expression getWhere() {
        return where;
    }

    public List<IPreparedQuery<T>> getPreparedQueries() {
        return preparedQueries;
    }
}
