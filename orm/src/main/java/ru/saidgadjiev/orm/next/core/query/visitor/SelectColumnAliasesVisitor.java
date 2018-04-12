package ru.saidgadjiev.orm.next.core.query.visitor;

import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumn;

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
