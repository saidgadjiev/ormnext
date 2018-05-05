package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

public interface EntityMetadataVisitor {

    boolean visit(DatabaseEntityMetadata<?> databaseEntityMetadata);

    void visit(ForeignColumnType foreignColumnType);

    void visit(ForeignCollectionColumnType foreignCollectionColumnType);

    void finish(ForeignColumnType foreignColumnType);

    void finish(ForeignCollectionColumnType foreignCollectionColumnType);
}
