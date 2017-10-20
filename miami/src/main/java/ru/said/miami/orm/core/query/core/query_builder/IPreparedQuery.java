package ru.said.miami.orm.core.query.core.query_builder;

import ru.said.miami.orm.core.query.core.Query;

public interface IPreparedQuery<T> {

    Query<T> prepare();
}
