package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazyList;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazySet;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.EntityProcessingState;

/**
 * Created by said on 23.07.2018.
 */
public class WriteCollection implements EntityMetadataVisitor {

    private ResultSetContext resultSetContext;

    private EntityProcessingState processingState;

    private CollectionLoader collectionLoader;

    WriteCollection(ResultSetContext resultSetContext,
                           EntityProcessingState processingState,
                           CollectionLoader collectionLoader) {
        this.resultSetContext = resultSetContext;
        this.processingState = processingState;
        this.collectionLoader = collectionLoader;
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
            loadEagerCollection(foreignCollectionColumnType);
        } else {
            loadLazyCollection(foreignCollectionColumnType);
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

    private void loadEagerCollection(ForeignCollectionColumnTypeImpl collectionColumnType) {
        Object instance = processingState.getEntityInstance();

        Optional<Set<Object>> collectionObjectIdsOptional = processingState.getCollectionObjectIds(
                collectionColumnType.getCollectionObjectClass()
        );

        if (collectionObjectIdsOptional.isPresent()) {
            for (Object collectionObjectId : collectionObjectIdsOptional.get()) {
                Object object = resultSetContext.getEntry(
                        collectionColumnType.getCollectionObjectClass(),
                        collectionObjectId
                );

                collectionColumnType.add(instance, object);
            }
        }
    }

    private void loadLazyCollection(ForeignCollectionColumnTypeImpl collectionColumnType) {
        Object instance = processingState.getEntityInstance();

        switch (collectionColumnType.getCollectionType()) {
            case LIST:
                collectionColumnType.assign(instance, new LazyList(
                        collectionLoader,
                        resultSetContext.getSession(),
                        processingState.getLazyCollectionOwnerKey(),
                        (List) collectionColumnType.access(instance))
                );
                break;
            case SET:
                collectionColumnType.assign(instance, new LazySet(
                        collectionLoader,
                        resultSetContext.getSession(),
                        processingState.getLazyCollectionOwnerKey(),
                        (Set) collectionColumnType.access(instance))
                );
                break;
            default:
                throw new RuntimeException(
                        "Unknown collection type " + collectionColumnType.getField().getType()
                );
        }
    }
}
