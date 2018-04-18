package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import java.util.Collection;
import java.util.List;

public class EntityAliasesImpl implements EntityAliases {

    private String tableAlias;

    private List<String> columnAliases;

    private String keyAlias;

    public EntityAliasesImpl(String tableAlias, List<String> columnAliases, String keyAlias) {
        this.tableAlias = tableAlias;
        this.columnAliases = columnAliases;
        this.keyAlias = keyAlias;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public List<String> getColumnAliases() {
        return columnAliases;
    }

    @Override
    public String getKeyAlias() {
        return keyAlias;
    }
}
