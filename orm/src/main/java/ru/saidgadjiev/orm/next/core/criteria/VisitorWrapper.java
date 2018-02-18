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

public class VisitorWrapper implements QueryVisitor {

    private QueryVisitor visitor;

    public VisitorWrapper(QueryVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public String getQuery() {
        return visitor.getQuery();
    }

    @Override
    public void visit(CreateQuery tCreateQuery, QueryVisitor visitor) {
        this.visitor.visit(tCreateQuery, visitor);
    }

    @Override
    public void visit(UpdateValue updateValue) {
        visitor.visit(updateValue);
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        visitor.visit(stringLiteral);
    }

    @Override
    public void visit(Select tSelectQuery, QueryVisitor visitor) {
        this.visitor.visit(tSelectQuery, visitor);
    }

    @Override
    public void visit(Expression expression, QueryVisitor visitor) {
        this.visitor.visit(expression, visitor);
    }

    @Override
    public void visit(AndCondition andCondition) {
        visitor.visit(andCondition);
    }

    @Override
    public void visit(Equals equals, QueryVisitor visitor) {
        this.visitor.visit(equals, visitor);
    }

    @Override
    public void visit(ColumnSpec columnSpec, QueryVisitor visitor) {
        this.visitor.visit(columnSpec, visitor);
    }

    @Override
    public void visit(TableRef tableRef, QueryVisitor visitor) {
        this.visitor.visit(tableRef, visitor);
    }

    @Override
    public void visit(AttributeDefinition attributeDefinition) {
        visitor.visit(attributeDefinition);
    }

    @Override
    public void visit(CreateTableQuery tCreateTableQuery, QueryVisitor visitor) {
        this.visitor.visit(tCreateTableQuery, visitor);
    }

    @Override
    public void visit(DeleteQuery deleteQuery) {
        visitor.visit(deleteQuery);
    }

    @Override
    public void visit(IntLiteral intLiteral) {
        visitor.visit(intLiteral);
    }

    @Override
    public boolean visit(UpdateQuery updateQuery, QueryVisitor visitor) {
        return this.visitor.visit(updateQuery, visitor);
    }

    @Override
    public void visit(DropTableQuery dropTableQuery) {
        visitor.visit(dropTableQuery);
    }

    @Override
    public void visit(PrimaryKeyConstraint primaryKeyConstraint) {
        visitor.visit(primaryKeyConstraint);
    }

    @Override
    public void visit(UniqueConstraint uniqueConstraint) {
        visitor.visit(uniqueConstraint);
    }

    @Override
    public void visit(NotNullConstraint notNullConstraint) {
        visitor.visit(notNullConstraint);
    }

    @Override
    public void visit(ReferencesConstraint referencesConstraint) {
        visitor.visit(referencesConstraint);
    }

    @Override
    public void visit(CreateIndexQuery createIndexQuery) {
        visitor.visit(createIndexQuery);
    }

    @Override
    public void visit(DropIndexQuery dropIndexQuery) {
        visitor.visit(dropIndexQuery);
    }

    @Override
    public void visit(Param param) {
        visitor.visit(param);
    }

    @Override
    public void visit(SelectAll selectAll) {
        visitor.visit(selectAll);
    }

    @Override
    public void visit(SelectColumnsList selectColumnsList, QueryVisitor visitor) {
        this.visitor.visit(selectColumnsList, visitor);
    }

    @Override
    public void visit(Having having) {
        visitor.visit(having);
    }

    @Override
    public void visit(GroupBy groupBy, QueryVisitor visitor) {
        this.visitor.visit(groupBy, visitor);
    }

    @Override
    public void visit(FromTable fromTable) {
        visitor.visit(fromTable);
    }

    @Override
    public void visit(LeftJoin leftJoin, QueryVisitor visitor) {
        this.visitor.visit(leftJoin, visitor);
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        visitor.visit(booleanLiteral);
    }

    @Override
    public void visit(JoinInfo joinInfo, QueryVisitor visitor) {
        this.visitor.visit(joinInfo, visitor);
    }

    @Override
    public void visit(CountAll countAll) {
        visitor.visit(countAll);
    }

    @Override
    public void visit(FromJoinedTables fromJoinedTables, QueryVisitor visitor) {
        this.visitor.visit(fromJoinedTables, visitor);
    }

    @Override
    public void visit(DisplayedColumns displayedColumns, QueryVisitor visitor) {
        this.visitor.visit(displayedColumns, visitor);
    }

    @Override
    public void visit(AVG avg) {
        visitor.visit(avg);
    }

    @Override
    public void visit(CountExpression countExpression, QueryVisitor visitor) {
        this.visitor.visit(countExpression, visitor);
    }

    @Override
    public void visit(MAX max) {
        visitor.visit(max);
    }

    @Override
    public void visit(MIN min) {
        visitor.visit(min);
    }

    @Override
    public void visit(Exists exists) {
        visitor.visit(exists);
    }

    @Override
    public void visit(InSelect inSelect, QueryVisitor visitor) {
        this.visitor.visit(inSelect, visitor);
    }

    @Override
    public void visit(NotInSelect notInSelect) {
        visitor.visit(notInSelect);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitor.visit(greaterThan);
    }

    @Override
    public void visit(GreaterThanOrEquals greaterThanOrEquals) {
        visitor.visit(greaterThanOrEquals);
    }

    @Override
    public void visit(LessThan lessThan) {
        visitor.visit(lessThan);
    }

    @Override
    public void visit(LessThanOrEquals lessThanOrEquals) {
        visitor.visit(lessThanOrEquals);
    }

    @Override
    public void visit(SUM sum, QueryVisitor visitor) {
        this.visitor.visit(sum, visitor);
    }

    @Override
    public void visit(OperandCondition operandCondition, QueryVisitor visitor) {
        this.visitor.visit(operandCondition, visitor);
    }

    @Override
    public void visit(Alias alias) {
        visitor.visit(alias);
    }

    @Override
    public void visit(DateLiteral dateLiteral) {
        visitor.visit(dateLiteral);
    }

    @Override
    public void visit(Default aDefault) {
        visitor.visit(aDefault);
    }

    @Override
    public void visit(DisplayedOperand displayedOperand) {
        visitor.visit(displayedOperand);
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        visitor.visit(floatLiteral);
    }

    @Override
    public void visit(DoubleLiteral doubleLiteral) {
        visitor.visit(doubleLiteral);
    }

    @Override
    public void visit(OrderBy orderBy) {
        visitor.visit(orderBy);
    }

    @Override
    public void visit(OrderByItem orderByItem, QueryVisitor visitor) {
        this.visitor.visit(orderByItem, visitor);
    }

    @Override
    public QueryVisitor getOriginal() {
        return visitor;
    }
}
