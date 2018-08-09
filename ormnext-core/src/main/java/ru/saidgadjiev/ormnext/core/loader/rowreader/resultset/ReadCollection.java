package ru.saidgadjiev.ormnext.core.loader.rowreader.resultset;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.CollectionContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;

import java.sql.SQLException;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.EntityProcessingState;

/**
 * Read collection values from result set.
 *
 * @author Said Gadjiev
 */
public class ReadCollection implements EntityMetadataVisitor {

    /**
     * Aliases.
     */
    private CollectionEntityAliases aliases;

    /**
     * Result set context.
     */
    private ResultSetContext resultSetContext;

    /**
     * Uid.
     */
    private String uid;

    /**
     * Create a new instance.
     *
     * @param collectionContext target collection context
     * @param resultSetContext  target result set context
     */
    ReadCollection(CollectionContext collectionContext, ResultSetContext resultSetContext) {
        this.aliases = collectionContext.getAliases();
        this.resultSetContext = resultSetContext;
        this.uid = collectionContext.getUid();
    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) throws SQLException {
        return true;
    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException {
        return false;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) throws SQLException {
        if (foreignCollectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            readEagerCollection(foreignCollectionColumnType);
        } else {
            readLazyCollection();
        }

        return false;
    }

    @Override
    public void finish(ForeignColumnTypeImpl foreignColumnType) {

    }

    @Override
    public void finish(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {

    }

    @Override
    public boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType) throws SQLException {
        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {

    }

    /**
     * Read eager values from result set.
     *
     * @param collectionColumnType target collection column type
     * @throws SQLException any SQL exceptions
     */
    private void readEagerCollection(ForeignCollectionColumnTypeImpl collectionColumnType) throws SQLException {
        if (resultSetContext.isResultColumn(aliases.getCollectionObjectKeyAlias())) {
            ResultSetValue collectionValue = resultSetContext.getCurrentRow().get(
                    aliases.getCollectionObjectKeyAlias()
            );

            if (collectionValue.wasNull()) {
                return;
            }

            Object collectionOwnerId = resultSetContext.getCurrentRow().get(
                    aliases.getCollectionOwnerColumnKeyAlias()
            ).getValue();
            EntityProcessingState processingState = resultSetContext.getProcessingState(
                    uid,
                    collectionOwnerId
            );

            processingState.addCollectionObjectId(
                    collectionColumnType.getCollectionObjectClass(),
                    collectionValue.getValue()
            );
        }
    }

    /**
     * Read lazy values from result set.
     *
     * @throws SQLException any SQL exceptions
     */
    private void readLazyCollection() throws SQLException {
        ResultSetValue collectionValue = resultSetContext.getCurrentRow().get(
                aliases.getCollectionOwnerColumnKeyAlias()
        );

        if (collectionValue.wasNull()) {
            return;
        }

        EntityProcessingState processingState = resultSetContext.getProcessingState(uid, collectionValue.getValue());

        if (processingState.getLazyCollectionOwnerKey() == null) {
            ResultSetValue ownerForeignKeyValue = processingState.getValues().get(
                    aliases.getCollectionObjectKeyAlias()
            );

            processingState.setLazyCollectionOwnerKey(ownerForeignKeyValue.getValue());
        }
    }
}
