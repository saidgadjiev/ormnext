package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;

public class DefaultCriteriaVisitor extends DefaultVisitor implements CriteriaVisitor {

    public DefaultCriteriaVisitor(DatabaseType databaseType) {
        super(databaseType);
    }

    @Override
    public void start(SelectCriteria selectCriteria) {
        sql.append("SELECT ");


        if (!selectCriteria.getWhere().getExpression().getConditions().isEmpty()) {
            sql.append(" WHERE ");
        }
    }

    @Override
    public void finish(SelectCriteria selectCriteria) {

    }
}
