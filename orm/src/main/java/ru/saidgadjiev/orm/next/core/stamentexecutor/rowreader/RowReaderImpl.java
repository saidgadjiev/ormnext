package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class RowReaderImpl implements RowReader {

    private Collection<EntityInitializer> entityInitializers;

    private EntityInitializer rootEntityInitializer;

    public RowReaderImpl(Collection<EntityInitializer> entityInitializers, EntityInitializer rootEntityInitializer) {
        this.entityInitializers = entityInitializers;
        this.rootEntityInitializer = rootEntityInitializer;
    }

    @Override
    public Object startRead(ResultSetContext resultSetContext) throws SQLException {
        for (EntityInitializer entityInitializer: entityInitializers) {
            entityInitializer.startRead(resultSetContext);
        }

        return resultSetContext.getProcessingState(rootEntityInitializer.getUid()).getEntityInstance();
    }

    @Override
    public void finishRead(ResultSetContext resultSetContext) throws SQLException {
        for (EntityInitializer entityInitializer: entityInitializers) {
            entityInitializer.finishRead(resultSetContext);
        }
    }
}
