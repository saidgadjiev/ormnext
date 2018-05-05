package ru.saidgadjiev.ormnext.core.query.visitor;

import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 08.04.2018.
 */
public class SelectColumnAliasesVisitor extends NoActionVisitor {

    private Map<String, String> columnAliasMap = new HashMap<>();

    @Override
    public boolean visit(DisplayedColumn displayedColumn) {
        ColumnSpec columnSpec = displayedColumn.getColumnSpec();

        columnAliasMap.put(columnSpec.getAlias().getAlias() + "." + columnSpec.getName(), displayedColumn.getAlias().getAlias());

        return false;
    }

    public Map<String, String> getColumnAliasMap() {
        return columnAliasMap;
    }
}
