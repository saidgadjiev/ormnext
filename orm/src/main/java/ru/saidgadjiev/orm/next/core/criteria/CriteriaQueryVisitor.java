package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.AndCondition;
import ru.saidgadjiev.orm.next.core.query.core.Select;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.condition.OperandCondition;
import ru.saidgadjiev.orm.next.core.query.core.function.SUM;
import ru.saidgadjiev.orm.next.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

/**
 * Created by said on 23.02.2018.
 */
public class CriteriaQueryVisitor extends NoActionVisitor {

    private final TableInfo<?> tableInfo;

    private final Alias alias;

    public CriteriaQueryVisitor(TableInfo<?> tableInfo, Alias alias) {
        this.tableInfo = tableInfo;
        this.alias = alias;
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
    public void visit(Having having) {
        having.getExpression().accept(this);
    }

    @Override
    public void visit(FromTable fromTable) {
        fromTable.getTableRef().accept(this);
    }

    @Override
    public void visit(SUM sum) {
        sum.getExpression().accept(this);
    }

    @Override
    public void visit(OperandCondition operandCondition) {
        operandCondition.getOperand().accept(this);
    }

}
