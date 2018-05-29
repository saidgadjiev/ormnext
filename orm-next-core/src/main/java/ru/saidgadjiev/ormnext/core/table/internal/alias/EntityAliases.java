package ru.saidgadjiev.ormnext.core.table.internal.alias;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity aliases.
 */
public class EntityAliases {

    /**
     * Table alias.
     */
    private final String tableAlias;

    /**
     * Column aliases.
     */
    private final Map<String, String> columnAliases;

    /**
     * Aliases by entity property names.
     */
    private final Map<String, String> propertyNameAliases;

    /**
     * Primary key alias.
     */
    private final String keyAlias;

    /**
     * Create a new instance.
     * @param tableAlias target table alias
     * @param columnAliases target columns aliases
     * @param propertyNameAliases target property names aliases
     * @param keyAlias target primary key alias
     */
    public EntityAliases(String tableAlias,
                         Map<String, String> columnAliases,
                         Map<String, String> propertyNameAliases,
                         String keyAlias) {
        this.tableAlias = tableAlias;
        this.columnAliases = columnAliases;
        this.propertyNameAliases = propertyNameAliases;
        this.keyAlias = keyAlias;
    }

    /**
     * Return table alias.
     * @return table alias
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * Column aliases.
     * @return column aliases
     */
    public List<String> getColumnAliases() {
        return new ArrayList<>(columnAliases.values());
    }

    /**
     * Primary key alias.
     * @return primary key alias
     */
    public String getKeyAlias() {
        return keyAlias;
    }

    /**
     * Return alias by column name.
     * @param columnName target column name
     * @return alias
     */
    public String getAliasByColumnName(String columnName) {
        return columnAliases.get(columnName);
    }

    /**
     * Return alias by propertyp name.
     * @param propertyName target property name
     * @return alias
     */
    public String getAliasByPropertyName(String propertyName) {
        return propertyNameAliases.get(propertyName);
    }
}
