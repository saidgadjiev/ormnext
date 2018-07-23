package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import java.sql.SQLException;

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
    void accept(EntityMetadataVisitor visitor) throws SQLException;
}
