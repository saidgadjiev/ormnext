package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.sql.SQLException;
import java.util.*;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.EntityProcessingState;

/**
 * This class represent entity initializer.
 * It initialize entity object with two phases algorithm.
 * It mean we read all retrieved entity column values from result set object and save them to
 * temporary context {@link ResultSetContext} it is a first phase.
 * Then we set read values from first phase to entity object it is a second phase.
 *
 * @author said gadjiev
 */
public class EntityInitializer {

    /**
     * Logger.
     */
    private static final Log LOG = LoggerFactory.getLogger(EntityInitializer.class);

    /**
     * Uid generated from {@link UIDGenerator}.
     */
    private final String uid;

    /**
     * Current initializer entity persister.
     *
     * @see DatabaseEntityPersister
     */
    private final DatabaseEntityPersister persister;

    /**
     * Current entity column aliases.
     *
     * @see EntityAliases
     */
    private final EntityAliases entityAliases;

    /**
     * Create a new instance.
     *
     * @param uid           target uid
     * @param entityAliases target entity aliases
     * @param persister     target entity persister
     */
    public EntityInitializer(String uid, EntityAliases entityAliases, DatabaseEntityPersister persister) {
        this.uid = uid;
        this.persister = persister;
        this.entityAliases = entityAliases;
    }

    /**
     * This method execute first phase operations.
     *
     * @param context current context
     * @return dummy entity object with only id
     * @throws SQLException any SQL exceptions
     */
    public Object startRead(ResultSetContext context) throws SQLException {
        IDatabaseColumnType idColumnType = persister.getMetadata().getPrimaryKeyColumnType();
        Object id = idColumnType.dataPersister().readValue(
                context.getDatabaseResults(),
                entityAliases.getKeyAlias()
        );

        if (context.getDatabaseResults().wasNull()) {
            return null;
        }
        EntityProcessingState processingState = context.getProcessingState(uid, id);
        Object entityInstance;

        LOG.debug("Processing %s id %s", persister.getMetadata().getTableClass().getName(), id);
        if (processingState.getEntityInstance() == null) {
            entityInstance = persister.instance();

            processingState.setNew(true);
            processingState.setEntityInstance(entityInstance);
            LOG.debug("Create new instance %s", persister.getMetadata().getTableClass().getName());
        } else {
            processingState.setNew(false);
            entityInstance = processingState.getEntityInstance();
        }
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        IDatabaseColumnType primaryKey = entityMetadata.getPrimaryKeyColumnType();

        primaryKey.assign(entityInstance, id);

        addToCache(context, id, entityInstance);
        List<ResultSetValue> values = new ArrayList<>();

        List<String> columnAliases = entityAliases.getColumnAliases();
        int i = 0;

        for (IDatabaseColumnType columnType : entityMetadata.getColumnTypes()) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }
            if (columnType.id()) {
                continue;
            }
            Object value = columnType.dataPersister().readValue(
                    context.getDatabaseResults(),
                    columnAliases.get(i++)
            );

            values.add(new ResultSetValue(value, context.getDatabaseResults().wasNull()));
        }
        processingState.setValuesFromResultSet(values);
        LOG.debug("Values read from resultset %s", values);

        return id;
    }

    /**
     * This method execute second phase operations for all read at first phase entity objects.
     *
     * @param context current context
     * @throws SQLException throw any SQL exceptions
     */
    public void finishRead(ResultSetContext context) throws SQLException {
        Map<Object, EntityProcessingState> processingStates = context.getProcessingStates(uid);

        if (processingStates != null) {
            for (Map.Entry<Object, EntityProcessingState> entry : processingStates.entrySet()) {
                finishRead(context, entry.getValue());
            }
        }
    }

    /**
     * This method execute second phase operations.
     *
     * @param processingState current processing state
     * @param context         current context
     * @throws SQLException throw any SQL exceptions
     */
    private void finishRead(ResultSetContext context, EntityProcessingState processingState) throws SQLException {
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        Object entityInstance = processingState.getEntityInstance();
        List<ResultSetValue> values = processingState.getValues();
        int i = 0;

        for (IDatabaseColumnType columnType : entityMetadata.getColumnTypes()) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }
            if (columnType.id()) {
                continue;
            }
            if (columnType.databaseColumnType()) {
                ResultSetValue resultSetValue = values.get(i++);
                Object value = resultSetValue.getValue();

                if (columnType.getColumnConverters().isPresent()) {
                    List<ColumnConverter<?, Object>> columnConverters = columnType.getColumnConverters().get();

                    for (ColumnConverter columnConverter : columnConverters) {
                        value = columnConverter.sqlToJava(value);
                    }
                }
                columnType.assign(entityInstance, value);
            }
            if (columnType.foreignColumnType()) {
                ForeignColumnType foreignColumnType = (ForeignColumnType) columnType;
                ResultSetValue resultSetValue = values.get(i++);

                if (!resultSetValue.isWasNull()) {
                    switch (foreignColumnType.getFetchType()) {
                        case LAZY:
                            LOG.debug(
                                    "Found lazy entity %s %s",
                                    foreignColumnType.getField().getDeclaringClass().getName(),
                                    foreignColumnType.getField().getName()
                            );
                            Object proxy = persister.createProxy(
                                    foreignColumnType.getForeignFieldClass(),
                                    resultSetValue.getValue()
                            );

                            columnType.assign(entityInstance, proxy);
                            break;
                        case EAGER:
                            columnType.assign(entityInstance, context.getEntry(
                                    foreignColumnType.getForeignFieldClass(),
                                    resultSetValue.getValue()
                            ));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * Add object to cache.
     *
     * @param context        target context
     * @param id             target object id
     * @param entityInstance target object
     */
    private void addToCache(ResultSetContext context, Object id, Object entityInstance) {
        context.addEntry(id, entityInstance);
        context.getCacheHelper().saveToCache(id, entityInstance);
    }

    /**
     * Return uid associated with this initializer.
     *
     * @return uid associated with this initializer
     */
    public String getUid() {
        return uid;
    }

    /**
     * Return aliases associated with this initializer.
     *
     * @return aliases associated with this initializer
     */
    public EntityAliases getEntityAliases() {
        return entityAliases;
    }
}
