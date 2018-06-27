package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.Alias;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * Visitor for visit criteria statement. Use for replace property name by column name with aliases.
 * Use it for {@link ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement}.
 *
 * @author Said Gadjiev
 */
public class SelectQuerySpaceVisitor extends NoActionVisitor {

    /**
     * Criteria entity metadata.
     */
    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    /**
     * Entity aliases.
     */
    private EntityAliases entityAliases;

    /**
     * Create a new visitor instance.
     *
     * @param databaseEntityMetadata criteria entity metadata
     * @param entityAliases          criteria entity aliases
     */
    public SelectQuerySpaceVisitor(DatabaseEntityMetadata<?> databaseEntityMetadata, EntityAliases entityAliases) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.entityAliases = entityAliases;
    }

    @Override
    public boolean visit(TableRef tableRef) {
        if (tableRef.getAlias() == null) {
            tableRef
                    .alias(entityAliases.getTableAlias());
        }

        return false;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        if (columnSpec.getAlias() == null) {
            String propertyName = columnSpec.getName();
            String columnName = databaseEntityMetadata.getPropertyColumnName(propertyName)
                    .orElseThrow(() -> new PropertyNotFoundException(databaseEntityMetadata.getTableClass(), propertyName));

            columnSpec
                    .name(columnName)
                    .alias(entityAliases.getTableAlias());
        }

        return false;
    }

    @Override
    public boolean visit(DisplayedColumn displayedColumn) {
        String propertyName = displayedColumn.getColumnSpec().getName();

        displayedColumn.getColumnSpec().accept(this);

        if (displayedColumn.getAlias() == null) {
            databaseEntityMetadata.checkProperty(propertyName);
            displayedColumn.setAlias(new Alias(entityAliases.getAliasByPropertyName(propertyName)));
        }

        return false;
    }
}
