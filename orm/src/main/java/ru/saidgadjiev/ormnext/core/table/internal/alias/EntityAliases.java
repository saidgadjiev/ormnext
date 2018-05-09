package ru.saidgadjiev.ormnext.core.table.internal.alias;

import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityAliases {

    private String tableAlias;

    private Map<String, String> columnAliases;

    private Map<String, String> propertyNameAliases;

    private String keyAlias;

    private final DataPersister<?> keyDataPersister;

    public EntityAliases(String tableAlias, Map<String, String> columnAliases, Map<String, String> propertyNameAliases, String keyAlias, DataPersister<?> keyDataPersister) {
        this.tableAlias = tableAlias;
        this.columnAliases = columnAliases;
        this.propertyNameAliases = propertyNameAliases;
        this.keyAlias = keyAlias;
        this.keyDataPersister = keyDataPersister;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public List<String> getColumnAliases() {
        return new ArrayList<>(columnAliases.values());
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getAliasByColumnName(String columnName) {
        return columnAliases.get(columnName);
    }

    public String getAliasByPropertyName(String propertyName) {
        return propertyNameAliases.get(propertyName);
    }
}
