package ru.saidgadjiev.ormnext.core.query.core.join;

import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class LeftJoin implements JoinExpression {

    private TableRef joinedTableRef;

    private Expression expression = new Expression();

    public LeftJoin(TableRef joinedTableRef) {
        this.joinedTableRef = joinedTableRef;
    }

    public LeftJoin(TableRef joinedTableRef, Expression expression) {
        this.joinedTableRef = joinedTableRef;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public TableRef getJoinedTableRef() {
        return joinedTableRef;
    }

    public void setJoinedTableRef(TableRef joinedTableRef) {
        this.joinedTableRef = joinedTableRef;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            joinedTableRef.accept(visitor);
            expression.accept(visitor);
        }
    }
}
