package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Alias;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedPropertyColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;

/**
 * Visitor for set property column aliases.
 *
 * @author Said Gadjiev
 */
public class SetPropertyColumnAliases extends NoActionVisitor {

    /**
     * Entity aliases.
     */
    private final EntityAliases entityAliases;

    /**
     * Create a new instance.
     *
     * @param entityAliases target aliases
     */
    public SetPropertyColumnAliases(EntityAliases entityAliases) {
        this.entityAliases = entityAliases;
    }

    @Override
    public boolean visit(DisplayedPropertyColumn displayedColumn) {
        if (!displayedColumn.isResolved()) {
            String columnName = displayedColumn.getColumnSpec().getName();

            displayedColumn.setAlias(new Alias(entityAliases.getAliasByColumnName(columnName)));
            displayedColumn.setResolved();
        }

        return true;
    }

    @Override
    public boolean visit(PropertyColumnSpec propertyColumnSpec) {
        propertyColumnSpec.alias(entityAliases.getTableAlias());

        return false;
    }
}
