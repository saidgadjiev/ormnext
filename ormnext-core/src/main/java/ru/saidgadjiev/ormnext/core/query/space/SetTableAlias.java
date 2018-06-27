package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;

/**
 * Created by said on 27.06.2018.
 */
public class SetTableAlias extends NoActionVisitor {

    private final String tableAlias;

    public SetTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    @Override
    public boolean visit(TableRef tableRef) {
        tableRef.alias(tableAlias);

        return false;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        columnSpec.alias(tableAlias);

        return false;
    }

    @Override
    public boolean visit(PropertyColumnSpec propertyColumnSpec) {
        propertyColumnSpec.alias(tableAlias);

        return false;
    }
}
