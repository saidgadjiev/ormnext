package ru.saidgadjiev.orm.next.core.criteria;

public interface CriteriaElement {
    void accept(CriteriaVisitor visitor);
}
