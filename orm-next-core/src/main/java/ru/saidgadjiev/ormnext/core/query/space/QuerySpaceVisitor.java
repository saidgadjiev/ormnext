package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import static ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils.getColumnNameByPropertyName;

/**
 * Visitor for visit criteria statement. Use for replace property name by column name with aliases.
 */
public class QuerySpaceVisitor extends NoActionVisitor {

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
     * @param databaseEntityMetadata criteria entity metadata
     * @param entityAliases criteria entity aliases
     */
    public QuerySpaceVisitor(DatabaseEntityMetadata<?> databaseEntityMetadata, EntityAliases entityAliases) {
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
            String columnName = getColumnNameByPropertyName(
                    databaseEntityMetadata.getColumnTypes(), propertyName
            ).orElseThrow(() -> new PropertyNotFoundException(databaseEntityMetadata.getTableClass(), propertyName));

            columnSpec
                    .name(columnName)
                    .alias(entityAliases.getTableAlias());
        }

        return false;
    }
}
