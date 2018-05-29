package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.field.field_type.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.field_type.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * Entity visitor. Represent visitor pattern.
 */
public interface EntityMetadataVisitor {

    /**
     * Start visit {@link DatabaseEntityMetadata} element.
     * @param databaseEntityMetadata target metadata
     * @return true if need visit another visitor elements that contained in requested entity element else false
     */
    boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata);

    /**
     * Start visit {@link ForeignColumnType} element.
     * @param foreignColumnType target column type
     */
    void start(ForeignColumnType foreignColumnType);

    /**
     * Start visit {@link ForeignCollectionColumnType} element.
     * @param foreignCollectionColumnType target column type
     */
    void start(ForeignCollectionColumnType foreignCollectionColumnType);

    /**
     * Finish visit {@link ForeignColumnType} element.
     * @param foreignColumnType target column type
     */
    void finish(ForeignColumnType foreignColumnType);

    /**
     * Finish visit {@link ForeignCollectionColumnType} element.
     * @param foreignCollectionColumnType target column type
     */
    void finish(ForeignCollectionColumnType foreignCollectionColumnType);
}
