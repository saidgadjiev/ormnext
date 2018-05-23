package ru.saidgadjiev.ormnext.core.query.visitor;

/**
 * Visitor pattern element. Use for visit implementation by visitor {@link QueryVisitor}.
 */
public interface QueryElement {

    /**
     * Method use for accept visitor.
     * @param visitor target visitor
     */
    void accept(QueryVisitor visitor);
}
