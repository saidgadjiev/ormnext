package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public interface CriteriaVisitor extends QueryVisitor {

    void start(SelectCriteria selectCriteria);

    void finish(SelectCriteria selectCriteria);
}
