package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
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
import java.util.Optional;
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
    private String uid;

    /**
     * Collection load helper class.
     *
     * @see CollectionLoader
     */
    private final CollectionLoader collectionLoader;

    /**
     * Collection query space.
     */
    private final CollectionQuerySpace querySpace;

    /**
     * Foreign collection column type.
     */
    private final ForeignCollectionColumnTypeImpl collectionColumnType;

    /**
     * Aliases.
     */
    private final CollectionEntityAliases aliases;

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

        querySpace = collectionLoader.getCollectionQuerySpace();
        collectionColumnType = querySpace.getForeignCollectionColumnType();
        aliases = querySpace.getCollectionEntityAliases();
    }

    /**
     * This method execute first phase operations.
     *
     * @param context current context
     * @throws SQLException any SQL exceptions
     */
    public void startRead(ResultSetContext context) throws SQLException {
        if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            readEagerCollection(context);
        } else {
            readLazyCollection(context);
        }
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
                loadCollection(resultSetContext, entry.getValue());
            }
        }
    }

    /**
     * This method execute second phase operations for read first phase collection object id.
     *
     * @param context context
     * @param state   processing state
     */
    private void loadCollection(ResultSetContext context, EntityProcessingState state) {
        if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            loadEagerCollection(context, state);
        } else {
            loadLazyCollection(context, state);
        }
    }

    /**
     * Handle start read for eager collection.
     *
     * @param context target context
     * @throws SQLException any SQL exceptions
     */
    private void readEagerCollection(ResultSetContext context) throws SQLException {
        DatabaseColumnType collectionObjectPrimaryKey = querySpace.getCollectionObjectPrimaryKey();
        DataPersister collectionObjectDataPersister = collectionObjectPrimaryKey.dataPersister();

        if (context.isResultColumn(aliases.getCollectionObjectKeyAlias())) {
            Object collectionObjectId = collectionObjectDataPersister.readValue(
                    context.getDatabaseResults(),
                    aliases.getCollectionObjectKeyAlias()
            );

            if (context.getDatabaseResults().wasNull()) {
                return;
            }
            EntityProcessingState processingState = getCurrentProcessingState(context);

            collectionObjectId = ArgumentUtils.processConvertersToJavaValue(
                    collectionObjectId,
                    collectionObjectPrimaryKey
            ).getValue();

            ForeignCollectionColumnTypeImpl columnType = querySpace.getForeignCollectionColumnType();

            processingState.addCollectionObjectId(columnType.getCollectionObjectClass(), collectionObjectId);
        }
    }

    /**
     * Read eager collection.
     *
     * @param context target context
     * @throws SQLException any SQL exceptions
     */
    private void readLazyCollection(ResultSetContext context) throws SQLException {
        EntityProcessingState processingState = getCurrentProcessingState(context);

        if (processingState != null && processingState.getLazyCollectionOwnerKey() == null) {
            ForeignColumnType foreignColumnType = collectionColumnType.getForeignColumnType();

            Object ownerForeignKey = foreignColumnType.dataPersister().readValue(
                    context.getDatabaseResults(),
                    aliases.getCollectionObjectKeyAlias()
            );

            if (context.getDatabaseResults().wasNull()) {
                ownerForeignKey = null;
            }
            ownerForeignKey = ArgumentUtils.processConvertersToJavaValue(
                    ownerForeignKey,
                    foreignColumnType
            ).getValue();

            processingState.setLazyCollectionOwnerKey(ownerForeignKey);
        }
    }

    /**
     * Load eager collection objects.
     *
     * @param context         target context
     * @param processingState target processing state
     */
    private void loadEagerCollection(ResultSetContext context, EntityProcessingState processingState) {
        Object instance = processingState.getEntityInstance();

        Optional<Set<Object>> collectionObjectIdsOptional = processingState.getCollectionObjectIds(
                collectionColumnType.getCollectionObjectClass()
        );

        if (collectionObjectIdsOptional.isPresent()) {
            for (Object collectionObjectId : collectionObjectIdsOptional.get()) {
                Object object = context.getEntry(
                        collectionColumnType.getCollectionObjectClass(),
                        collectionObjectId
                );

                collectionColumnType.add(instance, object);
            }
        }
    }

    /**
     * Load lazy collection objects.
     *
     * @param context target context
     * @param state   target processing state
     */
    private void loadLazyCollection(ResultSetContext context, EntityProcessingState state) {
        Object instance = state.getEntityInstance();
        Field field = collectionColumnType.getField();

        LOG.debug("Found lazy collection %s %s", field.getDeclaringClass().getName(), field.getName());
        switch (collectionColumnType.getCollectionType()) {
            case LIST:
                collectionColumnType.assign(instance, new LazyList(
                        collectionLoader,
                        context.getSession().getSessionManager(),
                        state.getLazyCollectionOwnerKey(),
                        (List) collectionColumnType.access(instance))
                );
                break;
            case SET:
                collectionColumnType.assign(instance, new LazySet(
                        collectionLoader,
                        context.getSession().getSessionManager(),
                        state.getLazyCollectionOwnerKey(),
                        (Set) collectionColumnType.access(instance))
                );
                break;
            default:
                throw new RuntimeException(
                        "Unknown collection type " + collectionColumnType.getField().getType()
                );
        }
    }

    /**
     * Return current processing state.
     *
     * @param context target context
     * @return processing state
     * @throws SQLException any SQL exceptions
     */
    private EntityProcessingState getCurrentProcessingState(ResultSetContext context) throws SQLException {
        Object collectionOwnerId = querySpace
                .getOwnerPrimaryKey()
                .dataPersister()
                .readValue(context.getDatabaseResults(), aliases.getCollectionOwnerColumnKeyAlias());

        if (context.getDatabaseResults().wasNull()) {
            return null;
        }
        collectionOwnerId = ArgumentUtils.processConvertersToJavaValue(
                collectionOwnerId,
                querySpace.getOwnerPrimaryKey()
        ).getValue();

        return context.getProcessingState(uid, collectionOwnerId);
    }
}
