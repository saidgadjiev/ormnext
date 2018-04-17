package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import java.util.Collection;
import java.util.List;

public class EntityAliasesImpl implements EntityAliases {

    private String tableAlias;

    private List<String> columnAliases;

    public EntityAliasesImpl(String tableAlias, List<String> columnAliases) {
        this.tableAlias = tableAlias;
        this.columnAliases = columnAliases;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public List<String> getColumnAliases() {
        return columnAliases;
    }
}
