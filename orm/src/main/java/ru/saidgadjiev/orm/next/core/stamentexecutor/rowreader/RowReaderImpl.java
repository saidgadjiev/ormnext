package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;

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
    public Object startRead(ResultSetContext resultSetContext) throws SQLException {
        rootEntityInitializer.startRead(resultSetContext);

        for (EntityInitializer entityInitializer: entityInitializers) {
            entityInitializer.startRead(resultSetContext);
        }

        return resultSetContext.getProcessingState(rootEntityInitializer.getUid()).getEntityInstance();
    }

    @Override
    public void finishRead(ResultSetContext resultSetContext) throws SQLException {
        rootEntityInitializer.finishRead(resultSetContext);

        for (EntityInitializer entityInitializer: entityInitializers) {
            entityInitializer.finishRead(resultSetContext);
        }
    }
}
