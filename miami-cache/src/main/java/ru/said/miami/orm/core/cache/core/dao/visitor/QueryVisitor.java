package ru.said.miami.orm.core.cache.core.dao.visitor;

import ru.said.miami.orm.core.cache.core.clause.query.InsertQuery;
import ru.said.miami.orm.core.cache.core.clause.query.SelectQuery;
import ru.said.miami.orm.core.cache.core.clause.table.CreateTable;
import ru.said.miami.orm.core.cache.core.clause.query.UpdateValue;

/**
 * Created by said on 17.06.17.
 */
public interface QueryVisitor {

    void start(SelectQuery selectQuery);

    void start(InsertQuery insertQuery);

    void start(UpdateValue updateValue);

    void finish(InsertQuery insertQuery);

    void finish(UpdateValue updateValue);

    String preparedQuery();

    void start(CreateTable createTable);
}
