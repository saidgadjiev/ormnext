package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import java.util.Collection;
import java.util.List;

public class EntityAliases {

    private String tableAlias;

    private List<String> columnAliases;

    private String keyAlias;

    public EntityAliases(String tableAlias, List<String> columnAliases, String keyAlias) {
        this.tableAlias = tableAlias;
        this.columnAliases = columnAliases;
        this.keyAlias = keyAlias;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public List<String> getColumnAliases() {
        return columnAliases;
    }

    public String getKeyAlias() {
        return keyAlias;
    }
}
