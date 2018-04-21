package ru.saidgadjiev.orm.next.core.dao.visitor;

import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

public interface EntityMetadataVisitor {

    boolean visit(DatabaseEntityMetadata<?> databaseEntityMetadata);

    void visit(ForeignColumnType foreignColumnType);

    void visit(ForeignCollectionColumnType foreignCollectionColumnType);

    void finish(ForeignColumnType foreignColumnType);

    void finish(ForeignCollectionColumnType foreignCollectionColumnType);
}
