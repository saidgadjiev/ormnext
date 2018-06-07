package ru.saidgadjiev.ormnext.core.table.internal.visitor;

/**
 * Visitor element for visitor pattern.
 *
 * @author Said Gadjiev
 */
public interface EntityElement {

    /**
     * Method use for accept visitor.
     * @param visitor target visitor
     */
    void accept(EntityMetadataVisitor visitor);
}
