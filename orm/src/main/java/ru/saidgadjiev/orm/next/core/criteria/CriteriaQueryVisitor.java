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
import ru.saidgadjiev.orm.next.core.table.TableInfo;

/**
 * Created by said on 23.02.2018.
 */
public class CriteriaQueryVisitor implements QueryVisitor {

    private final TableInfo<?> tableInfo;

    private final Alias alias;

    public CriteriaQueryVisitor(TableInfo<?> tableInfo, Alias alias) {
        this.tableInfo = tableInfo;
        this.alias = alias;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public void visit(CreateQuery tCreateQuery) {

    }

    @Override
    public void visit(UpdateValue updateValue) {

    }

    @Override
    public void visit(StringLiteral stringLiteral) {

    }

    @Override
    public void visit(Select tSelectQuery) {
        tSelectQuery.getFrom().accept(this);
    }

    @Override
    public void visit(Expression expression) {
        for (AndCondition andCondition: expression.getConditions()) {
            for (Condition condition: andCondition.getConditions()) {
                condition.accept(this);
            }
        }
    }

    @Override
    public void visit(AndCondition andCondition) {

    }

    @Override
    public void visit(Equals equals) {
        equals.getFirst().accept(this);
        equals.getSecond().accept(this);
    }

    @Override
    public void visit(ColumnSpec columnSpec) {
        columnSpec.alias(alias).name(tableInfo.getPersistenceName(columnSpec.getName()));
    }

    @Override
    public void visit(TableRef tableRef) {
        tableRef.alias(alias);
    }

    @Override
    public void visit(AttributeDefinition attributeDefinition) {

    }

    @Override
    public void visit(CreateTableQuery tCreateTableQuery) {

    }

    @Override
    public void visit(DeleteQuery deleteQuery) {

    }

    @Override
    public void visit(IntLiteral intLiteral) {

    }

    @Override
    public boolean visit(UpdateQuery updateQuery) {
        return false;
    }

    @Override
    public void visit(DropTableQuery dropTableQuery) {

    }

    @Override
    public void visit(PrimaryKeyConstraint primaryKeyConstraint) {

    }

    @Override
    public void visit(UniqueConstraint uniqueConstraint) {

    }

    @Override
    public void visit(NotNullConstraint notNullConstraint) {

    }

    @Override
    public void visit(ReferencesConstraint referencesConstraint) {

    }

    @Override
    public void visit(CreateIndexQuery createIndexQuery) {

    }

    @Override
    public void visit(DropIndexQuery dropIndexQuery) {

    }

    @Override
    public void visit(Param param) {

    }

    @Override
    public void visit(SelectAll selectAll) {

    }

    @Override
    public void visit(SelectColumnsList selectColumnsList) {

    }

    @Override
    public void visit(Having having) {
        having.getExpression().accept(this);
    }

    @Override
    public void visit(GroupBy groupBy) {

    }

    @Override
    public void visit(FromTable fromTable) {
        fromTable.getTableRef().accept(this);
    }

    @Override
    public void visit(LeftJoin leftJoin) {

    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {

    }

    @Override
    public void visit(JoinInfo joinInfo) {

    }

    @Override
    public void visit(CountAll countAll) {

    }

    @Override
    public void visit(FromJoinedTables fromJoinedTables) {

    }

    @Override
    public void visit(DisplayedColumns displayedColumns) {

    }

    @Override
    public void visit(AVG avg) {

    }

    @Override
    public void visit(CountExpression countExpression) {

    }

    @Override
    public void visit(MAX max) {

    }

    @Override
    public void visit(MIN min) {

    }

    @Override
    public void visit(Exists exists) {

    }

    @Override
    public void visit(InSelect inSelect) {

    }

    @Override
    public void visit(NotInSelect notInSelect) {

    }

    @Override
    public void visit(GreaterThan greaterThan) {

    }

    @Override
    public void visit(GreaterThanOrEquals greaterThanOrEquals) {

    }

    @Override
    public void visit(LessThan lessThan) {

    }

    @Override
    public void visit(LessThanOrEquals lessThanOrEquals) {

    }

    @Override
    public void visit(SUM sum) {
        sum.getExpression().accept(this);
    }

    @Override
    public void visit(OperandCondition operandCondition) {
        operandCondition.getOperand().accept(this);
    }

    @Override
    public void visit(Alias alias) {

    }

    @Override
    public void visit(DateLiteral dateLiteral) {

    }

    @Override
    public void visit(Default aDefault) {

    }

    @Override
    public void visit(DisplayedOperand displayedOperand) {

    }

    @Override
    public void visit(FloatLiteral floatLiteral) {

    }

    @Override
    public void visit(DoubleLiteral doubleLiteral) {

    }

    @Override
    public void visit(OrderBy orderBy) {

    }

    @Override
    public void visit(OrderByItem orderByItem, QueryVisitor visitor) {

    }
}
