package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazyList;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazySet;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.EntityProcessingState;

/**
 * This class use for initialize entity column type
 * annotated with {@link ru.saidgadjiev.ormnext.core.field.ForeignCollectionField}.
 * It use two phases algorithm for initialize collection.
 * First phase:
 * Read all collection object ids.
 * Second phase:
 * Retrieve collection objects by them ids and add them to column type collection.
 *
 * @author said gadjiev
 */
public class CollectionInitializer {

    /**
     * Logger.
     */
    private static final Log LOG = LoggerFactory.getLogger(CollectionInitializer.class);

    /**
     * Collection owner uid generated with {@link UIDGenerator}.
     */
    private final String uid;

    /**
     * Aliases associated with this initializer.
     *
     * @see CollectionEntityAliases
     */
    private final CollectionEntityAliases aliases;

    /**
     * Collection load helper class.
     *
     * @see CollectionLoader
     */
    private final CollectionLoader collectionLoader;

    /**
     * Entity persister associated for collection object id.
     *
     * @see DataPersister
     */
    private final DataPersister foreignColumnDataPersister;

    /**
     * Create a new instance.
     *
     * @param ownerUID         owner uid
     * @param collectionLoader collection load helper
     */
    public CollectionInitializer(String ownerUID,
                                 CollectionLoader collectionLoader) {
        this.uid = ownerUID;
        this.aliases = collectionLoader.getCollectionEntityAliases();
        this.collectionLoader = collectionLoader;
        this.foreignColumnDataPersister = collectionLoader.getOwnerPrimaryKeyPersister();
    }

    /**
     * This method execute first phase operations.
     *
     * @param context current context
     * @param id      collection owner id
     * @throws SQLException any SQL exceptions
     */
    public void startRead(ResultSetContext context, Object id) throws SQLException {
        EntityProcessingState processingState = context.getProcessingState(uid, id);
        Object collectionObjectId = foreignColumnDataPersister.readValue(
                context.getDatabaseResults(),
                aliases.getCollectionOwnerColumnKeyAlias()
        );

        if (context.getDatabaseResults().wasNull()) {
            return;
        }
        processingState.addCollectionObjectId(collectionObjectId);
    }

    /**
     * This method execute second phase operations for all read first phase collection object ids.
     *
     * @param resultSetContext context
     * @throws SQLException any SQL exceptions
     */
    public void loadCollection(ResultSetContext resultSetContext) throws SQLException {
        Map<Object, EntityProcessingState> processingStates = resultSetContext.getProcessingStates(uid);

        if (processingStates != null) {
            for (Map.Entry<Object, EntityProcessingState> entry : processingStates.entrySet()) {
                loadCollection(resultSetContext, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * This method execute second phase operations for read first phase collection object id.
     *
     * @param resultSetContext context
     * @param processingState  processing state
     * @param id               current id
     * @throws SQLException any SQL exceptions
     */
    private void loadCollection(ResultSetContext resultSetContext, Object id, EntityProcessingState processingState)
            throws SQLException {
        Object instance = processingState.getEntityInstance();
        ForeignCollectionColumnType foreignCollectionColumnType = collectionLoader.getGoreignCollectionColumnType();

        if (foreignCollectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            for (Object collectionObjectId : processingState.getCollectionObjectIds()) {
                foreignCollectionColumnType.add(instance, resultSetContext.getSession().queryForId(
                        foreignCollectionColumnType.getCollectionObjectClass(),
                        collectionObjectId
                ));
            }
        } else {
            Field field = foreignCollectionColumnType.getField();

            LOG.debug("Found lazy collection %s %s", field.getDeclaringClass().getName(), field.getName());
            switch (foreignCollectionColumnType.getCollectionType()) {
                case LIST:
                    foreignCollectionColumnType.assign(instance, new LazyList(
                            collectionLoader,
                            resultSetContext.getSession().getSessionManager(),
                            id,
                            (List) foreignCollectionColumnType.access(instance))
                    );
                    break;
                case SET:
                    foreignCollectionColumnType.assign(instance, new LazySet(
                            collectionLoader,
                            resultSetContext.getSession().getSessionManager(),
                            id,
                            (Set) foreignCollectionColumnType.access(instance))
                    );
                    break;
                default:
                    throw new RuntimeException(
                            "Unknown collection type " + foreignCollectionColumnType.getField().getType()
                    );
            }
        }
    }
}
