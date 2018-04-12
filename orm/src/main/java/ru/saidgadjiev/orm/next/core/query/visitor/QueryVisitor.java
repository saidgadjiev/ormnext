package ru.saidgadjiev.orm.next.core.query.visitor;

import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromSubQuery;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumn;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.condition.*;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.Default;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.NotNullConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.PrimaryKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.orm.next.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.orm.next.core.query.core.function.*;
import ru.saidgadjiev.orm.next.core.query.core.join.JoinInfo;
import ru.saidgadjiev.orm.next.core.query.core.join.LeftJoin;
import ru.saidgadjiev.orm.next.core.query.core.literals.*;

/**
 * Паттерн Visitor
 */
public interface QueryVisitor {

    boolean visit(CreateQuery tCreateQuery);

    boolean visit(UpdateValue updateValue);

    void visit(StringLiteral stringLiteral);

    boolean visit(Select tSelectQuery);

    boolean visit(Expression expression);

    boolean visit(AndCondition andCondition);

    boolean visit(Equals equals);

    boolean visit(ColumnSpec columnSpec);

    boolean visit(TableRef tableRef);

    boolean visit(AttributeDefinition attributeDefinition);

    boolean visit(CreateTableQuery tCreateTableQuery);

    boolean visit(DeleteQuery deleteQuery);

    void visit(IntLiteral intLiteral);

    boolean visit(UpdateQuery updateQuery);

    void visit(DropTableQuery dropTableQuery);

    void visit(PrimaryKeyConstraint primaryKeyConstraint);

    void visit(UniqueConstraint uniqueConstraint);

    void visit(NotNullConstraint notNullConstraint);

    void visit(ReferencesConstraint referencesConstraint);

    void visit(CreateIndexQuery createIndexQuery);

    void visit(DropIndexQuery dropIndexQuery);

    void visit(Param param);

    void visit(SelectAll selectAll);

    boolean visit(SelectColumnsList selectColumnsList);

    boolean visit(Having having);

    boolean visit(GroupBy groupBy);

    boolean visit(FromTable fromTable);

    boolean visit(LeftJoin leftJoin);

    void visit(BooleanLiteral booleanLiteral);

    boolean visit(JoinInfo joinInfo);

    void visit(CountAll countAll);

    boolean visit(FromJoinedTables fromJoinedTables);

    boolean visit(DisplayedColumn displayedColumn);

    boolean visit(AVG avg);

    boolean visit(CountExpression countExpression);

    boolean visit(MAX max);

    boolean visit(MIN min);

    boolean visit(Exists exists);

    boolean visit(InSelect inSelect);

    boolean visit(NotInSelect notInSelect);

    boolean visit(GreaterThan greaterThan);

    boolean visit(GreaterThanOrEquals greaterThanOrEquals);

    boolean visit(LessThan lessThan);

    boolean visit(LessThanOrEquals lessThanOrEquals);

    boolean visit(SUM sum);

    boolean visit(OperandCondition operandCondition);

    void visit(Alias alias);

    void visit(DateLiteral dateLiteral);

    boolean visit(Default aDefault);

    boolean visit(DisplayedOperand displayedOperand);

    void visit(FloatLiteral floatLiteral);

    void visit(DoubleLiteral doubleLiteral);

    boolean visit(OrderBy orderBy);

    boolean visit(OrderByItem orderByItem, QueryVisitor visitor);

    void visit(Limit limit);

    void visit(Offset offset);

    boolean visit(NotNull notNull);

    boolean visit(IsNull isNull);

    boolean visit(NotEquals notEquals);

    boolean visit(Like like);

    boolean visit(Between between);

    boolean visit(Not not);

    boolean visit(NotInValues notInValues);

    boolean visit(InValues inValues);

    boolean visit(ForeignKeyConstraint foreignKeyConstraint);

    boolean visit(FromSubQuery fromSubQuery);

    boolean visit(GroupByItem groupByItem);

    boolean visit(OrderByItem orderByItem);
}
