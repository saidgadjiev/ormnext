package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader;

import ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionInitializer;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.EntityInitializer;

import java.sql.SQLException;
import java.util.Collection;

public class RowReaderImpl implements RowReader {

    private Collection<EntityInitializer> entityInitializers;

    private Collection<CollectionInitializer> collectionInitializers;

    private EntityInitializer rootEntityInitializer;

    public RowReaderImpl(Collection<EntityInitializer> entityInitializers, Collection<CollectionInitializer> collectionInitializers, EntityInitializer rootEntityInitializer) {
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
        ResultSetContext.EntityProcessingState entityProcessingState = resultSetContext.getProcessingState(rootEntityInitializer.getUid(), readedId);

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
