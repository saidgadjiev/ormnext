package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntityAliases {

    private String tableAlias;

    private Map<String, String> columnAliases;

    private String keyAlias;

    public EntityAliases(String tableAlias, Map<String, String> columnAliases, String keyAlias) {
        this.tableAlias = tableAlias;
        this.columnAliases = columnAliases;
        this.keyAlias = keyAlias;
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

    public String getAliasByPropertyName(String property) {
        return columnAliases.get(property);
    }
}
