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
     * @throws SQLException any SQL exceptions
     */
    boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) throws SQLException;

    /**
     * Finish visit {@link DatabaseEntityMetadata} element.
     *
     * @param entityMetadata target column type
     */
    void finish(DatabaseEntityMetadata<?> entityMetadata);

    /**
     * Start visit {@link ForeignColumnTypeImpl} element.
     *
     * @param foreignColumnType target column type
     * @return true if need visit finish method
     * @throws SQLException any SQL exceptions
     */
    boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException;

    /**
     * Start visit {@link ForeignCollectionColumnTypeImpl} element.
     *
     * @param foreignCollectionColumnType target column type
     * @return true if need visit finish method
     * @throws SQLException any SQL exceptions
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

    /**
     * Start visit {@link SimpleDatabaseColumnTypeImpl} element.
     *
     * @param databaseColumnType target column type
     * @return true if need visit finish method
     * @throws SQLException any SQL exceptions
     */
    boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType) throws SQLException;

    /**
     * Finish visit {@link SimpleDatabaseColumnTypeImpl} element.
     *
     * @param databaseColumnType target column type
     */
    void finish(SimpleDatabaseColumnTypeImpl databaseColumnType);
}
