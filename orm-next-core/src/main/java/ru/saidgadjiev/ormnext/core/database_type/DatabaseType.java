package ru.saidgadjiev.ormnext.core.database_type;

import ru.saidgadjiev.ormnext.core.query_element.AttributeDefinition;

/**
 * Definition of the per-database functionality needed to isolate the differences between the various databases.
 */
public interface DatabaseType {

    /**
     * Primary auto generated definition part.
     * @param generated true if is generated
     * @return primary key definition
     */
    String getPrimaryKeyDefinition(boolean generated);

    /**
     * No args insert definition.
     * @return no args insert definition
     */
    String getNoArgsInsertDefinition();

    /**
     * Escape entity name string.
     * @return escape entity name string
     */
    String getEntityNameEscape();

    /**
     * Escape value string.
     * @return escape value string
     */
    String getValueEscape();

    /**
     * Return type definition.
     * @param def target attribute definition
     * @return return type definition
     * @see AttributeDefinition
     */
    String getTypeSqlPresent(AttributeDefinition def);
}
