package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * Visitor for resolve property columns.
 *
 * @author Said Gadjiev
 */
public class ResolvePropertyColumn extends NoActionVisitor {

    /**
     * Entity metadata.
     */
    private final DatabaseEntityMetadata<?> entityMetadata;

    /**
     * Create a new instance.
     *
     * @param entityMetadata target metadata
     */
    public ResolvePropertyColumn(DatabaseEntityMetadata<?> entityMetadata) {
        this.entityMetadata = entityMetadata;
    }

    @Override
    public synchronized boolean visit(PropertyColumnSpec propertyColumnSpec) {
        if (!propertyColumnSpec.isResolved()) {
            String propertyName = propertyColumnSpec.getName();
            String columnName = entityMetadata.getPropertyColumnName(propertyName)
                    .orElseThrow(() -> new PropertyNotFoundException(entityMetadata.getTableClass(), propertyName));

            propertyColumnSpec.name(columnName);
            propertyColumnSpec.setResolved();
        }

        return false;
    }
}
