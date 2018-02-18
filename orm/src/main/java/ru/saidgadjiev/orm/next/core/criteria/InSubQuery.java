package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.condition.Condition;
import ru.saidgadjiev.orm.next.core.query.core.condition.InSelect;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

/**
 * Created by said on 18.02.2018.
 */
public class InSubQuery implements Condition {

    private Alias alias;

    private InSelect inSelect;

    public InSubQuery(Alias alias, InSelect inSelect) {
        this.alias = alias;
        this.inSelect = inSelect;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        inSelect.accept(new VisitorWrapper(visitor.getOriginal()) {

            @Override
            public void visit(ColumnSpec columnSpec, QueryVisitor visitor) {
                columnSpec.alias(alias);
                super.visit(columnSpec, visitor);
            }
        });
    }
}
