package ru.said.miami.orm.core.query.core.clause.from;

import ru.said.miami.orm.core.query.core.common.TableRef;
import ru.said.miami.orm.core.query.core.join.JoinExpression;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class FromJoinedTables implements FromExpression {

    private TableRef tableRef;

    private List<JoinExpression> joinExpressions = new ArrayList<>();

    public FromJoinedTables(TableRef tableRef) {
        this.tableRef = tableRef;
    }

    public TableRef getTableRef() {
        return tableRef;
    }

    public List<JoinExpression> getJoinExpression() {
        return joinExpressions;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }

}
