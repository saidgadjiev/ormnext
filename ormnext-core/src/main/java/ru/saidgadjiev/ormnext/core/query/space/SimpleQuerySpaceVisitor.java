package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * Visitor for visit criteria statement. Use for replace property name by column name with aliases.
 * Use for simple statements which not need aliases.
 *
 * @author Said Gadjiev
 */
public class SimpleQuerySpaceVisitor extends NoActionVisitor {

    /**
     * Criteria entity metadata.
     */
    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    /**
     * Create a new visitor instance.
     *
     * @param databaseEntityMetadata criteria entity metadata
     */
    public SimpleQuerySpaceVisitor(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        this.databaseEntityMetadata = databaseEntityMetadata;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        if (columnSpec.getAlias() == null) {
            String propertyName = columnSpec.getName();
            String columnName = databaseEntityMetadata.getPropertyColumnName(propertyName
            ).orElseThrow(() -> new PropertyNotFoundException(databaseEntityMetadata.getTableClass(), propertyName));

            columnSpec
                    .name(columnName);
        }

        return false;
    }
}
