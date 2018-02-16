package ru.saidgadjiev.orm.next.core.query.visitor;

import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.GroupBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderByItem;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumns;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.condition.*;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.Default;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.NotNullConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.PrimaryKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.orm.next.core.query.core.function.*;
import ru.saidgadjiev.orm.next.core.query.core.join.JoinInfo;
import ru.saidgadjiev.orm.next.core.query.core.join.LeftJoin;
import ru.saidgadjiev.orm.next.core.query.core.literals.*;

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

    void start(PrimaryKeyConstraint primaryKeyConstraint);

    void finish(PrimaryKeyConstraint primaryKeyConstraint);

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

    void start(DateLiteral dateLiteral);

    void finish(DateLiteral dateLiteral);

    void start(Default aDefault);

    void finish(Default aDefault);

    void start(DisplayedOperand displayedOperand);

    void finish(DisplayedOperand displayedOperand);

    void start(FloatLiteral floatLiteral);

    void finish(FloatLiteral floatLiteral);

    void start(DoubleLiteral doubleLiteral);

    void finish(DoubleLiteral doubleLiteral);

    void start(OrderBy orderBy);

    void finish(OrderBy orderBy);

    void start(OrderByItem orderByItem);

    void finish(OrderByItem orderByItem);

}
