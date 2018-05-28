package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.CollectionInitializer;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityInitializer;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Reader implementation.
 */
public class RowReaderImpl implements RowReader {

    /**
     * Entity initializers.
     * @see EntityInitializer
     */
    private Collection<EntityInitializer> entityInitializers;

    /**
     * Collection initializers.
     * @see CollectionInitializer
     */
    private Collection<CollectionInitializer> collectionInitializers;

    /**
     * Root entity initializer.
     * @see EntityInitializer
     */
    private EntityInitializer rootEntityInitializer;

    /**
     * Create a new row reader.
     * @param entityInitializers entity initializers
     * @param collectionInitializers collection initializers
     * @param rootEntityInitializer root entity initializer
     */
    public RowReaderImpl(Collection<EntityInitializer> entityInitializers,
                         Collection<CollectionInitializer> collectionInitializers,
                         EntityInitializer rootEntityInitializer) {
        this.entityInitializers = entityInitializers;
        this.collectionInitializers = collectionInitializers;
        this.rootEntityInitializer = rootEntityInitializer;
    }

    @Override
    public RowResult<Object> startRead(ResultSetContext resultSetContext) throws SQLException {
        Object readedId = rootEntityInitializer.startRead(resultSetContext);

        for (EntityInitializer entityInitializer : entityInitializers) {
            entityInitializer.startRead(resultSetContext);
        }
        for (CollectionInitializer collectionInitializer: collectionInitializers) {
            collectionInitializer.startRead(resultSetContext, readedId);
        }
        ResultSetContext.EntityProcessingState entityProcessingState = resultSetContext.getProcessingState(
                rootEntityInitializer.getUid(),
                readedId
        );

        return new RowResult<>(
                entityProcessingState.getEntityInstance(),
                entityProcessingState.isNew()
        );
    }

    @Override
    public void finishRead(ResultSetContext resultSetContext) throws SQLException {
        rootEntityInitializer.finishRead(resultSetContext);

        for (EntityInitializer entityInitializer : entityInitializers) {
            entityInitializer.finishRead(resultSetContext);
        }
        for (CollectionInitializer collectionInitializer : collectionInitializers) {
            collectionInitializer.loadCollection(resultSetContext);
        }
    }
}
