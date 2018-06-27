package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.PropertyColumnSpec;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * Created by said on 27.06.2018.
 */
public class ResolvePropertyColumn extends NoActionVisitor {

    private final DatabaseEntityMetadata<?> entityMetadata;

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
