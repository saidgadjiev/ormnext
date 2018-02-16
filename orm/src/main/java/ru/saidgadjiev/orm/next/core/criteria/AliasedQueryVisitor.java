package ru.saidgadjiev.orm.next.core.criteria;

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
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class AliasedQueryVisitor implements QueryVisitor {

    private Alias alias;

    private QueryVisitor visitor;

    public AliasedQueryVisitor(Alias alias, QueryVisitor visitor) {
        this.alias = alias;
        this.visitor = visitor;
    }

    @Override
    public String getQuery() {
        return visitor.getQuery();
    }

    @Override
    public boolean start(CreateQuery tCreateQuery) {
        return visitor.start(tCreateQuery);
    }

    @Override
    public void finish(CreateQuery tCreateQuery) {
        visitor.finish(tCreateQuery);
    }

    @Override
    public void start(UpdateValue updateValue) {
        visitor.start(updateValue);
    }

    @Override
    public void finish(UpdateValue updateValue) {
        visitor.finish(updateValue);
    }

    @Override
    public void start(StringLiteral stringLiteral) {
        visitor.start(stringLiteral);
    }

    @Override
    public void finish(StringLiteral stringLiteral) {
        visitor.finish(stringLiteral);
    }

    @Override
    public void start(Select tSelectQuery) {
        visitor.start(tSelectQuery);
    }

    @Override
    public void finish(Select tSelectQuery) {
        visitor.finish(tSelectQuery);
    }

    @Override
    public void start(Expression expression) {
        visitor.start(expression);
    }

    @Override
    public void finish(Expression expression) {
        visitor.finish(expression);
    }

    @Override
    public void start(AndCondition andCondition) {
        visitor.start(andCondition);
    }

    @Override
    public void finish(AndCondition andCondition) {
        visitor.finish(andCondition);
    }

    @Override
    public void start(Equals equals) {
        visitor.start(equals);
    }

    @Override
    public void finish(Equals equals) {
        visitor.finish(equals);
    }

    @Override
    public void start(ColumnSpec columnSpec) {
        alias.accept(this);
        visitor.start(columnSpec);
    }

    @Override
    public void finish(ColumnSpec columnSpec) {
        visitor.finish(columnSpec);
    }

    @Override
    public void finish(TableRef tableRef) {
        visitor.finish(tableRef);
    }

    @Override
    public void start(TableRef tableRef) {
        visitor.start(tableRef);
    }

    @Override
    public void start(AttributeDefinition attributeDefinition) {
        visitor.start(attributeDefinition);
    }

    @Override
    public void start(CreateTableQuery tCreateTableQuery) {
        visitor.start(tCreateTableQuery);
    }

    @Override
    public void finish(CreateTableQuery tCreateTableQuery) {
        visitor.finish(tCreateTableQuery);
    }

    @Override
    public void finish(AttributeDefinition attributeDefinition) {
        visitor.finish(attributeDefinition);
    }

    @Override
    public void start(DeleteQuery deleteQuery) {
        visitor.start(deleteQuery);
    }

    @Override
    public void finish(DeleteQuery deleteQuery) {
        visitor.finish(deleteQuery);
    }

    @Override
    public void start(IntLiteral intLiteral) {
        visitor.start(intLiteral);
    }

    @Override
    public void finish(IntLiteral intLiteral) {
        visitor.finish(intLiteral);
    }

    @Override
    public boolean start(UpdateQuery updateQuery) {
        return visitor.start(updateQuery);
    }

    @Override
    public void finish(UpdateQuery updateQuery) {
        visitor.finish(updateQuery);
    }

    @Override
    public void start(DropTableQuery dropTableQuery) {
        visitor.start(dropTableQuery);
    }

    @Override
    public void finish(DropTableQuery dropTableQuery) {
        visitor.finish(dropTableQuery);
    }

    @Override
    public void start(PrimaryKeyConstraint primaryKeyConstraint) {
        visitor.start(primaryKeyConstraint);
    }

    @Override
    public void finish(PrimaryKeyConstraint primaryKeyConstraint) {
        visitor.finish(primaryKeyConstraint);
    }

    @Override
    public void start(UniqueConstraint uniqueConstraint) {
        visitor.start(uniqueConstraint);
    }

    @Override
    public void finish(UniqueConstraint uniqueConstraint) {
        visitor.finish(uniqueConstraint);
    }

    @Override
    public void start(NotNullConstraint notNullConstraint) {
        visitor.start(notNullConstraint);
    }

    @Override
    public void finish(NotNullConstraint notNullConstraint) {
        visitor.finish(notNullConstraint);
    }

    @Override
    public void start(ReferencesConstraint referencesConstraint) {
        visitor.start(referencesConstraint);
    }

    @Override
    public void finish(ReferencesConstraint referencesConstraint) {
        visitor.finish(referencesConstraint);
    }

    @Override
    public void start(CreateIndexQuery createIndexQuery) {
        visitor.start(createIndexQuery);
    }

    @Override
    public void finish(CreateIndexQuery createIndexQuery) {
        visitor.finish(createIndexQuery);
    }

    @Override
    public void start(DropIndexQuery dropIndexQuery) {
        visitor.start(dropIndexQuery);
    }

    @Override
    public void finish(DropIndexQuery dropIndexQuery) {
        visitor.finish(dropIndexQuery);
    }

    @Override
    public void start(Param param) {
        visitor.start(param);
    }

    @Override
    public void finish(Param param) {
        visitor.finish(param);
    }

    @Override
    public void start(SelectAll selectAll) {
        visitor.start(selectAll);
    }

    @Override
    public void finish(SelectAll selectAll) {
        visitor.finish(selectAll);
    }

    @Override
    public void start(SelectColumnsList selectColumnsList) {
        visitor.start(selectColumnsList);
    }

    @Override
    public void finish(SelectColumnsList selectColumnsList) {
        visitor.finish(selectColumnsList);
    }

    @Override
    public void start(Having having) {
        visitor.start(having);
    }

    @Override
    public void finish(GroupBy groupBy) {
        visitor.finish(groupBy);
    }

    @Override
    public void start(GroupBy groupBy) {
        visitor.start(groupBy);
    }

    @Override
    public void finish(Having having) {
        visitor.finish(having);
    }

    @Override
    public void finish(FromTable fromTable) {
        visitor.finish(fromTable);
    }

    @Override
    public void start(FromTable fromTable) {
        visitor.start(fromTable);
    }

    @Override
    public void start(LeftJoin leftJoin) {
        visitor.start(leftJoin);
    }

    @Override
    public void finish(LeftJoin leftJoin) {
        visitor.finish(leftJoin);
    }

    @Override
    public void finish(BooleanLiteral booleanLiteral) {
        visitor.finish(booleanLiteral);
    }

    @Override
    public void start(BooleanLiteral booleanLiteral) {
        visitor.start(booleanLiteral);
    }

    @Override
    public void start(JoinInfo joinInfo) {
        visitor.start(joinInfo);
    }

    @Override
    public void finish(JoinInfo joinInfo) {
        visitor.finish(joinInfo);
    }

    @Override
    public void start(CountAll countAll) {
        visitor.start(countAll);
    }

    @Override
    public void finish(CountAll countAll) {
        visitor.finish(countAll);
    }

    @Override
    public void start(FromJoinedTables fromJoinedTables) {
        visitor.start(fromJoinedTables);
    }

    @Override
    public void finish(FromJoinedTables fromJoinedTables) {
        visitor.finish(fromJoinedTables);
    }

    @Override
    public void start(DisplayedColumns displayedColumns) {
        visitor.start(displayedColumns);
    }

    @Override
    public void finish(DisplayedColumns displayedColumns) {
        visitor.finish(displayedColumns);
    }

    @Override
    public void start(AVG avg) {
        visitor.start(avg);
    }

    @Override
    public void finish(AVG avg) {
        visitor.finish(avg);
    }

    @Override
    public void start(CountExpression countExpression) {
        visitor.start(countExpression);
    }

    @Override
    public void finish(CountExpression countExpression) {
        visitor.finish(countExpression);
    }

    @Override
    public void start(MAX max) {
        visitor.start(max);
    }

    @Override
    public void finish(MAX max) {
        visitor.finish(max);
    }

    @Override
    public void start(MIN min) {
        visitor.start(min);
    }

    @Override
    public void finish(MIN min) {
        visitor.finish(min);
    }

    @Override
    public void start(Exists exists) {
        visitor.start(exists);
    }

    @Override
    public void finish(Exists exists) {
        visitor.finish(exists);
    }

    @Override
    public void start(InSelect inSelect) {
        visitor.start(inSelect);
    }

    @Override
    public void finish(InSelect inSelect) {
        visitor.finish(inSelect);
    }

    @Override
    public void start(NotInSelect notInSelect) {
        visitor.start(notInSelect);
    }

    @Override
    public void finish(NotInSelect notInSelect) {
        visitor.finish(notInSelect);
    }

    @Override
    public void start(GreaterThan greaterThan) {
        visitor.start(greaterThan);
    }

    @Override
    public void finish(GreaterThan greaterThan) {
        visitor.finish(greaterThan);
    }

    @Override
    public void start(GreaterThanOrEquals greaterThanOrEquals) {
        visitor.start(greaterThanOrEquals);
    }

    @Override
    public void finish(GreaterThanOrEquals greaterThanOrEquals) {
        visitor.finish(greaterThanOrEquals);
    }

    @Override
    public void start(LessThan lessThan) {
        visitor.start(lessThan);
    }

    @Override
    public void finish(LessThan lessThan) {
        visitor.finish(lessThan);
    }

    @Override
    public void start(LessThanOrEquals lessThanOrEquals) {
        visitor.start(lessThanOrEquals);
    }

    @Override
    public void finish(LessThanOrEquals lessThanOrEquals) {
        visitor.finish(lessThanOrEquals);
    }

    @Override
    public void start(SUM sum) {
        visitor.start(sum);
    }

    @Override
    public void finish(SUM sum) {
        visitor.finish(sum);
    }

    @Override
    public void start(OperandCondition operandCondition) {
        visitor.start(operandCondition);
    }

    @Override
    public void finish(OperandCondition operandCondition) {
        visitor.finish(operandCondition);
    }

    @Override
    public void start(Alias alias) {
        visitor.start(alias);
    }

    @Override
    public void finish(Alias alias) {
        visitor.finish(alias);
    }

    @Override
    public void start(DateLiteral dateLiteral) {
        visitor.start(dateLiteral);
    }

    @Override
    public void finish(DateLiteral dateLiteral) {
        visitor.finish(dateLiteral);
    }

    @Override
    public void start(Default aDefault) {
        visitor.start(aDefault);
    }

    @Override
    public void finish(Default aDefault) {
        visitor.finish(aDefault);
    }

    @Override
    public void start(DisplayedOperand displayedOperand) {
        visitor.start(displayedOperand);
    }

    @Override
    public void finish(DisplayedOperand displayedOperand) {
        visitor.finish(displayedOperand);
    }

    @Override
    public void start(FloatLiteral floatLiteral) {
        visitor.start(floatLiteral);
    }

    @Override
    public void finish(FloatLiteral floatLiteral) {
        visitor.finish(floatLiteral);
    }

    @Override
    public void start(DoubleLiteral doubleLiteral) {
        visitor.start(doubleLiteral);
    }

    @Override
    public void finish(DoubleLiteral doubleLiteral) {
        visitor.finish(doubleLiteral);
    }

    @Override
    public void start(OrderBy orderBy) {
        visitor.start(orderBy);
    }

    @Override
    public void finish(OrderBy orderBy) {
        visitor.finish(orderBy);
    }

    @Override
    public void start(OrderByItem orderByItem) {
        visitor.start(orderByItem);
    }

    @Override
    public void finish(OrderByItem orderByItem) {
        visitor.finish(orderByItem);
    }
}
