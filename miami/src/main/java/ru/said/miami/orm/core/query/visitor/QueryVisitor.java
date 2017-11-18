package ru.said.miami.orm.core.query.visitor;

import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.query.core.clause.from.FromJoinedTables;
import ru.said.miami.orm.core.query.core.clause.from.FromTable;
import ru.said.miami.orm.core.query.core.clause.select.SelectAll;
import ru.said.miami.orm.core.query.core.clause.select.SelectColumnsList;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.columnSpec.DisplayedColumns;
import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.common.UpdateValue;
import ru.said.miami.orm.core.query.core.condition.*;
import ru.said.miami.orm.core.query.core.constraints.attribute.GeneratedConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.NotNullConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.said.miami.orm.core.query.core.constraints.table.UniqueConstraint;
import ru.said.miami.orm.core.query.core.function.*;
import ru.said.miami.orm.core.query.core.join.JoinInfo;
import ru.said.miami.orm.core.query.core.join.LeftJoin;
import ru.said.miami.orm.core.query.core.literals.BooleanLiteral;
import ru.said.miami.orm.core.query.core.literals.IntLiteral;
import ru.said.miami.orm.core.query.core.literals.Param;
import ru.said.miami.orm.core.query.core.literals.StringLiteral;
import ru.said.miami.orm.core.query.core.clause.*;

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

    void start(SelectColumnsList selectColumnsList);

    void finish(SelectColumnsList selectColumnsList);

    void start(Having having);

    void finish(GroupBy groupBy);

    void start(GroupBy groupBy);

    void finish(Having having);

    void finish(FromTable fromTable);

    void start(FromTable fromTable);

    void start(LeftJoin leftJoin);

    void finish(LeftJoin leftJoin);

    void finish(BooleanLiteral booleanLiteral);

    void start(BooleanLiteral booleanLiteral);

    void start(JoinInfo joinInfo);

    void finish(JoinInfo joinInfo);

    void start(CountAll countAll);

    void finish(CountAll countAll);

    void start(FromJoinedTables fromJoinedTables);

    void finish(FromJoinedTables fromJoinedTables);

    void start(DisplayedColumns displayedColumns);

    void finish(DisplayedColumns displayedColumns);

    void start(AVG avg);

    void finish(AVG avg);

    void start(CountExpression countExpression);

    void finish(CountExpression countExpression);

    void start(MAX max);

    void finish(MAX max);

    void start(MIN min);

    void finish(MIN min);

    void start(Exists exists);

    void finish(Exists exists);

    void start(InSelect inSelect);

    void finish(InSelect inSelect);

    void start(NotInSelect notInSelect);

    void finish(NotInSelect notInSelect);

    void start(GreaterThan greaterThan);

    void finish(GreaterThan greaterThan);

    void start(GreaterThanOrEquals greaterThanOrEquals);

    void finish(GreaterThanOrEquals greaterThanOrEquals);

    void start(LessThan lessThan);

    void finish(LessThan lessThan);

    void start(LessThanOrEquals lessThanOrEquals);

    void finish(LessThanOrEquals lessThanOrEquals);

    void start(SUM sum);

    void finish(SUM sum);

    void start(OperandCondition operandCondition);

    void finish(OperandCondition operandCondition);

    void start(Alias alias);

    void finish(Alias alias);
}
