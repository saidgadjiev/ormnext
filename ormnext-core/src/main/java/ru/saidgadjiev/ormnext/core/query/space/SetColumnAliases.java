package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Alias;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;

/**
 * Created by said on 27.06.2018.
 */
public class SetColumnAliases extends NoActionVisitor {

    private final EntityAliases entityAliases;

    public SetColumnAliases(EntityAliases entityAliases) {
        this.entityAliases = entityAliases;
    }

    @Override
    public boolean visit(TableRef tableRef) {
        tableRef.alias(entityAliases.getTableAlias());

        return false;
    }


    @Override
    public boolean visit(DisplayedColumn displayedColumn) {
        String columnName = displayedColumn.getColumnSpec().getName();

        displayedColumn.setAlias(new Alias(entityAliases.getAliasByColumnName(columnName)));

        return false;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        columnSpec.alias(entityAliases.getTableAlias());

        return false;
    }

    @Override
    public boolean visit(PropertyColumnSpec propertyColumnSpec) {
        propertyColumnSpec.alias(entityAliases.getTableAlias());

        return false;
    }
}
