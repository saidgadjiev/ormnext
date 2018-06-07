package ru.saidgadjiev.ormnext.core.dialect;

import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;

/**
 * Definition of the per-database functionality needed to isolate the differences between the various databases.
 *
 * @author Said Gadjiev
 */
public interface Dialect {

    /**
     * Primary auto generated definition part.
     *
     * @param generated true if is generated
     * @return primary key definition
     */
    String getPrimaryKeyDefinition(boolean generated);

    /**
     * No args insert definition.
     *
     * @return no args insert definition
     */
    String getNoArgsInsertDefinition();

    /**
     * Escape entity name string.
     *
     * @return escape entity name string
     */
    String getEntityNameEscape();

    /**
     * Escape value string.
     *
     * @return escape value string
     */
    String getValueEscape();

    /**
     * Return type definition.
     *
     * @param def target attribute definition
     * @return return type definition
     * @see AttributeDefinition
     */
    String getTypeSqlPresent(AttributeDefinition def);

    /**
     * True if support table foreign keys.
     *
     * @return true if support table foreign keys
     */
    boolean supportTableForeignConstraint();

    /**
     * True if support table unique constraints.
     *
     * @return true if support table unique constraints.
     */
    boolean supportTableUniqueConstraint();
}
