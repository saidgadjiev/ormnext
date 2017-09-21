package ru.said.miami.orm.core.query.visitor;

import ru.said.miami.orm.core.query.core.*;

/**
 * Паттерн Visitor
 */
public interface QueryVisitor {

    String getQuery();

    void start(CreateQuery tCreateQuery);

    void finish(CreateQuery tCreateQuery);

    void start(UpdateValue updateValue);

    void finish(UpdateValue updateValue);

    void start(StringLiteral stringLiteral);

    void finish(StringLiteral stringLiteral);

    void start(SelectQuery tSelectQuery);

    void finish(SelectQuery tSelectQuery);

    void start(Expression expression);

    void finish(Expression expression);

    void start(AndCondition andCondition);

    void finish(AndCondition andCondition);

    void start(Equals equals);

    void finish(Equals equals);

    void start(ColumnSpec columnSpec);

    void finish(ColumnSpec columnSpec);

    void finish(TableRef tableRef);

    void start(TableRef tableRef);

    void start(AttributeDefenition attributeDefenition);

    void start(CreateTableQuery tCreateTableQuery);

    void finish(CreateTableQuery tCreateTableQuery);

    void finish(AttributeDefenition attributeDefenition);
}
