package ru.said.miami.orm.core.query.visitor;

import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.query.core.conditions.Equals;
import ru.said.miami.orm.core.query.core.conditions.Expression;
import ru.said.miami.orm.core.query.core.constraints.attribute.GeneratedConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.NotNullConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.said.miami.orm.core.query.core.constraints.table.UniqueConstraint;
import ru.said.miami.orm.core.query.core.defenitions.AttributeDefinition;
import ru.said.miami.orm.core.query.core.literals.IntLiteral;
import ru.said.miami.orm.core.query.core.literals.Param;
import ru.said.miami.orm.core.query.core.literals.StringLiteral;
import ru.said.miami.orm.core.query.core.sqlQuery.*;

/**
 * Паттерн Visitor
 */
public interface QueryVisitor {

    String getQuery();

    boolean start(CreateQuery tCreateQuery);

    void finish(CreateQuery tCreateQuery);

    void start(UpdateValue updateValue);

    void finish(UpdateValue updateValue);

    void start(StringLiteral stringLiteral);

    void finish(StringLiteral stringLiteral);

    void start(Select tSelectQuery);

    void finish(Select tSelectQuery);

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

    void start(AttributeDefinition attributeDefinition);

    void start(CreateTableQuery tCreateTableQuery);

    void finish(CreateTableQuery tCreateTableQuery);

    void finish(AttributeDefinition attributeDefinition);

    void start(DeleteQuery deleteQuery);

    void finish(DeleteQuery deleteQuery);

    void start(IntLiteral intLiteral);

    void finish(IntLiteral intLiteral);

    boolean start(UpdateQuery updateQuery);

    void finish(UpdateQuery updateQuery);

    void start(DropTableQuery dropTableQuery);

    void finish(DropTableQuery dropTableQuery);

    void start(GeneratedConstraint generatedConstraint);

    void finish(GeneratedConstraint generatedConstraint);

    void start(UniqueConstraint uniqueConstraint);

    void finish(UniqueConstraint uniqueConstraint);

    void start(NotNullConstraint notNullConstraint);

    void finish(NotNullConstraint notNullConstraint);

    void start(ReferencesConstraint referencesConstraint);

    void finish(ReferencesConstraint referencesConstraint);

    void start(CreateIndexQuery createIndexQuery);

    void finish(CreateIndexQuery createIndexQuery);

    void start(DropIndexQuery dropIndexQuery);

    void finish(DropIndexQuery dropIndexQuery);

    void start(Param param);

    void finish(Param param);

    void start(SelectAll selectAll);

    void finish(SelectAll selectAll);

    void start(SelectColumns selectColumns);

    void finish(SelectColumns selectColumns);
}
