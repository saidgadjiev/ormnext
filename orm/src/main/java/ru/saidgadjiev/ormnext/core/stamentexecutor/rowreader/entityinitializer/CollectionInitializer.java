package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.ormnext.core.stamentexecutor.object.collection.LazyList;
import ru.saidgadjiev.ormnext.core.stamentexecutor.object.collection.LazySet;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionInitializer {

    private static final Log LOG = LoggerFactory.getLogger(CollectionInitializer.class);

    private String uid;

    private final CollectionEntityAliases aliases;

    private CollectionLoader collectionLoader;

    private DataPersister<?> foreignColumnDataPersister;

    public CollectionInitializer(String ownerUID,
                                 CollectionLoader collectionLoader) {
        this.uid = ownerUID;
        this.aliases = collectionLoader.getCollectionEntityAliases();
        this.collectionLoader = collectionLoader;
        this.foreignColumnDataPersister = collectionLoader.getOwnerPrimaryKeyPersister();
    }

    public void startRead(ResultSetContext context, Object id) throws SQLException {
        ResultSetContext.EntityProcessingState processingState = context.getProcessingState(uid, id);
        Object collectionObjectId = foreignColumnDataPersister.readValue(context.getDatabaseResults(), aliases.getCollectionColumnKeyAlias());

        if (context.getDatabaseResults().wasNull()) {
            return;
        }
        processingState.addCollectionObjectId(collectionObjectId);
    }

    public void loadCollection(ResultSetContext resultSetContext) throws SQLException {
        Map<Object, ResultSetContext.EntityProcessingState> processingStates = resultSetContext.getProcessingStates(uid);

        if (processingStates != null) {
            for (Map.Entry<Object, ResultSetContext.EntityProcessingState> entry : processingStates.entrySet()) {
                loadCollection(resultSetContext, entry.getKey(), entry.getValue());
            }
        }
    }

    private void loadCollection(ResultSetContext resultSetContext, Object id, ResultSetContext.EntityProcessingState processingState) throws SQLException {
        Object instance = processingState.getEntityInstance();
        ForeignCollectionColumnType foreignCollectionColumnType = collectionLoader.getGoreignCollectionColumnType();

        if (foreignCollectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            for (Object collectionObjectId : processingState.getCollectionObjectIds()) {
                foreignCollectionColumnType.add(instance, resultSetContext.getDao().queryForId(foreignCollectionColumnType.getCollectionObjectClass(), collectionObjectId));
            }
        } else {
            Field field = foreignCollectionColumnType.getField();

            //LOG.debug("Found lazy collection " + field.getDeclaringClass().getName() + " " + field.getName());

            try {
                switch (foreignCollectionColumnType.getCollectionType()) {
                    case LIST:
                        foreignCollectionColumnType.assign(instance, new LazyList(
                                collectionLoader,
                                resultSetContext.getDao().getSessionManager(),
                                id,
                                (List) foreignCollectionColumnType.access(instance))
                        );
                        break;
                    case SET:
                        foreignCollectionColumnType.assign(instance, new LazySet(
                                collectionLoader,
                                resultSetContext.getDao().getSessionManager(),
                                id,
                                (Set) foreignCollectionColumnType.access(instance))
                        );
                        break;
                    case UNDEFINED:
                        throw new RuntimeException("Unknown collection type " + foreignCollectionColumnType.getField().getType());
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }
}
