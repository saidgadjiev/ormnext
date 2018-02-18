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

    void visit(CreateQuery tCreateQuery, QueryVisitor visitor);

    void visit(UpdateValue updateValue);

    void visit(StringLiteral stringLiteral);

    void visit(Select tSelectQuery, QueryVisitor visitor);

    void visit(Expression expression, QueryVisitor visitor);

    void visit(AndCondition andCondition);

    void visit(Equals equals, QueryVisitor visitor);

    void visit(ColumnSpec columnSpec, QueryVisitor visitor);

    void visit(TableRef tableRef, QueryVisitor visitor);

    void visit(AttributeDefinition attributeDefinition);

    void visit(CreateTableQuery tCreateTableQuery, QueryVisitor visitor);

    void visit(DeleteQuery deleteQuery);

    void visit(IntLiteral intLiteral);

    boolean visit(UpdateQuery updateQuery, QueryVisitor visitor);

    void visit(DropTableQuery dropTableQuery);

    void visit(PrimaryKeyConstraint primaryKeyConstraint);

    void visit(UniqueConstraint uniqueConstraint);

    void visit(NotNullConstraint notNullConstraint);

    void visit(ReferencesConstraint referencesConstraint);

    void visit(CreateIndexQuery createIndexQuery);

    void visit(DropIndexQuery dropIndexQuery);

    void visit(Param param);

    void visit(SelectAll selectAll);

    void visit(SelectColumnsList selectColumnsList, QueryVisitor visitor);

    void visit(Having having);

    void visit(GroupBy groupBy, QueryVisitor visitor);

    void visit(FromTable fromTable);

    void visit(LeftJoin leftJoin, QueryVisitor visitor);

    void visit(BooleanLiteral booleanLiteral);

    void visit(JoinInfo joinInfo, QueryVisitor visitor);

    void visit(CountAll countAll);

    void visit(FromJoinedTables fromJoinedTables, QueryVisitor visitor);

    void visit(DisplayedColumns displayedColumns, QueryVisitor visitor);

    void visit(AVG avg);

    void visit(CountExpression countExpression, QueryVisitor visitor);

    void visit(MAX max);

    void visit(MIN min);

    void visit(Exists exists);

    void visit(InSelect inSelect, QueryVisitor visitor);

    void visit(NotInSelect notInSelect);

    void visit(GreaterThan greaterThan);

    void visit(GreaterThanOrEquals greaterThanOrEquals);

    void visit(LessThan lessThan);

    void visit(LessThanOrEquals lessThanOrEquals);

    void visit(SUM sum, QueryVisitor visitor);

    void visit(OperandCondition operandCondition, QueryVisitor visitor);

    void visit(Alias alias);

    void visit(DateLiteral dateLiteral);

    void visit(Default aDefault);

    void visit(DisplayedOperand displayedOperand);

    void visit(FloatLiteral floatLiteral);

    void visit(DoubleLiteral doubleLiteral);

    void visit(OrderBy orderBy);

    void visit(OrderByItem orderByItem, QueryVisitor visitor);

    default QueryVisitor getOriginal() {
        return this;
    }
}
