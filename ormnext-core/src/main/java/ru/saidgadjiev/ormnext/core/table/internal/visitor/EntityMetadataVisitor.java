package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.sql.SQLException;

/**
 * Entity visitor. Represent visitor pattern.
 *
 * @author Said Gadjiev
 */
public interface EntityMetadataVisitor {

    /**
     * Start visit {@link DatabaseEntityMetadata} element.
     *
     * @param databaseEntityMetadata target metadata
     * @return true if need visit another visitor elements that contained in requested entity element else false
     */
    boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata)  throws SQLException;

    /**
     * Start visit {@link ForeignColumnTypeImpl} element.
     *
     * @param foreignColumnType target column type
     * @return true if need visit finish method
     */
    boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException;

    /**
     * Start visit {@link ForeignCollectionColumnTypeImpl} element.
     *
     * @param foreignCollectionColumnType target column type
     * @return true if need visit finish method
     */
    boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) throws SQLException;

    /**
     * Finish visit {@link ForeignColumnTypeImpl} element.
     *
     * @param foreignColumnType target column type
     */
    void finish(ForeignColumnTypeImpl foreignColumnType);

    /**
     * Finish visit {@link ForeignCollectionColumnTypeImpl} element.
     *
     * @param foreignCollectionColumnType target column type
     */
    void finish(ForeignCollectionColumnTypeImpl foreignCollectionColumnType);

    boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType)  throws SQLException;

    void finish(SimpleDatabaseColumnTypeImpl databaseColumnType);
}
