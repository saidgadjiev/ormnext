package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazyList;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazySet;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.space.CollectionQuerySpace;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;

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
 * @author Said Gadjiev
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
     * Collection load helper class.
     *
     * @see CollectionLoader
     */
    private final CollectionLoader collectionLoader;

    /**
     * Create a new instance.
     *
     * @param ownerUID         owner uid
     * @param collectionLoader collection load helper
     */
    public CollectionInitializer(String ownerUID,
                                 CollectionLoader collectionLoader) {
        this.uid = ownerUID;
        this.collectionLoader = collectionLoader;
    }

    /**
     * This method execute first phase operations.
     *
     * @param context current context
     * @throws SQLException any SQL exceptions
     */
    public void startRead(ResultSetContext context) throws SQLException {
        CollectionQuerySpace collectionQuerySpace = collectionLoader.getCollectionQuerySpace();
        CollectionEntityAliases aliases = collectionQuerySpace.getCollectionEntityAliases();
        DatabaseColumnType collectionObjectPrimaryKey = collectionQuerySpace.getCollectionObjectPrimaryKey();
        DataPersister collectionObjectDataPersister = collectionObjectPrimaryKey.dataPersister();

        Object collectionObjectId = collectionObjectDataPersister.readValue(
                context.getDatabaseResults(),
                aliases.getCollectionObjectKeyAlias()
        );

        if (context.getDatabaseResults().wasNull()) {
            return;
        }
        Object collectionOwnerId = collectionQuerySpace
                .getOwnerPrimaryKey()
                .dataPersister()
                .readValue(context.getDatabaseResults(), aliases.getForeignColumnAlias());

        collectionOwnerId = ArgumentUtils.processConvertersToJavaValue(
                collectionOwnerId,
                collectionQuerySpace.getOwnerPrimaryKey()
        ).getValue();
        EntityProcessingState processingState = context.getProcessingState(uid, collectionOwnerId);

        collectionObjectId = ArgumentUtils.processConvertersToJavaValue(
                collectionObjectId,
                collectionObjectPrimaryKey
        ).getValue();
        ForeignCollectionColumnTypeImpl collectionColumnType = collectionQuerySpace.getForeignCollectionColumnType();

        processingState.addCollectionObjectId(collectionColumnType.getCollectionObjectClass(), collectionObjectId);
    }

    /**
     * This method execute second phase operations for all read first phase collection object ids.
     *
     * @param resultSetContext context
     */
    public void loadCollection(ResultSetContext resultSetContext) {
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
     */
    private void loadCollection(ResultSetContext resultSetContext, Object id, EntityProcessingState processingState) {
        CollectionQuerySpace collectionQuerySpace = collectionLoader.getCollectionQuerySpace();
        Object instance = processingState.getEntityInstance();
        ForeignCollectionColumnTypeImpl collectionColumnType = collectionQuerySpace.getForeignCollectionColumnType();

        if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            processingState.getCollectionObjectIds(collectionColumnType.getCollectionObjectClass())
                    .ifPresent(objects -> {
                        for (Object collectionObjectId : objects) {
                            collectionColumnType.add(instance, resultSetContext.getEntry(
                                    collectionColumnType.getCollectionObjectClass(),
                                    collectionObjectId
                            ));
                        }
                    });
        } else {
            Field field = collectionColumnType.getField();

            LOG.debug("Found lazy collection %s %s", field.getDeclaringClass().getName(), field.getName());
            switch (collectionColumnType.getCollectionType()) {
                case LIST:
                    collectionColumnType.assign(instance, new LazyList(
                            collectionLoader,
                            resultSetContext.getSession().getSessionManager(),
                            id,
                            (List) collectionColumnType.access(instance))
                    );
                    break;
                case SET:
                    collectionColumnType.assign(instance, new LazySet(
                            collectionLoader,
                            resultSetContext.getSession().getSessionManager(),
                            id,
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
}
