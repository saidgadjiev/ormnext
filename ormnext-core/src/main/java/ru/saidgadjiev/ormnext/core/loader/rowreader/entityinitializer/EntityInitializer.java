package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.EntityProcessingState;

/**
 * This class represent entity initializer.
 * It initialize entity object with two phases algorithm.
 * It mean we read all retrieved entity column values from result set object and save them to
 * temporary context {@link ResultSetContext} it is a first phase.
 * Then we set read values from first phase to entity object it is a second phase.
 *
 * @author Said Gadjiev
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
     * Current initializer entity metadata.
     */
    private final DatabaseEntityMetadata<?> metadata;

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

        metadata = persister.getMetadata();
    }

    /**
     * This method execute first phase operations.
     *
     * @param context current context
     * @return dummy entity object with only id
     * @throws SQLException any SQL exceptions
     */
    public Object startRead(ResultSetContext context) throws SQLException {
        DatabaseColumnType idColumnType = persister.getMetadata().getPrimaryKeyColumnType();

        if (!context.isResultColumn(entityAliases.getKeyAlias())) {
            return null;
        }
        Object id = idColumnType.dataPersister().readValue(
                context.getDatabaseResults(),
                entityAliases.getKeyAlias()
        );

        if (context.getDatabaseResults().wasNull()) {
            return null;
        }
        id = ArgumentUtils.processConvertersToJavaValue(id, idColumnType).getValue();

        EntityProcessingState processingState = context.getProcessingState(uid, id);
        LOG.debug("Processing %s id %s", persister.getMetadata().getTableClass().getName(), id);

        Object entityInstance = createOrGetEntity(processingState);
        DatabaseColumnType primaryKey = metadata.getPrimaryKeyColumnType();

        primaryKey.assign(entityInstance, id);

        if (context.getEntry(entityInstance.getClass(), id) == null) {
            context.addEntry(id, entityInstance);
            context.putToCache(id, entityInstance);
        }

        List<ResultSetValue> values = readResultSet(context, entityInstance);

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
        List<String> aliases = entityAliases.getColumnAliases();
        int valueIndex = 0;
        int aliasIndex = 0;

        for (DatabaseColumnType columnType : entityMetadata.getColumnTypes()) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }
            if (columnType.id()) {
                ++aliasIndex;
                continue;
            }
            String alias = aliases.get(aliasIndex++);

            if (context.isResultColumn(alias)) {
                ResultSetValue resultSetValue = values.get(valueIndex++);

                if (columnType.databaseColumnType()) {
                    Object value = resultSetValue.getValue();

                    columnType.assign(entityInstance, value);
                }
                if (columnType.foreignColumnType()) {
                    ForeignColumnTypeImpl foreignColumnType = (ForeignColumnTypeImpl) columnType;

                    if (!resultSetValue.isWasNull()) {
                        switch (foreignColumnType.getFetchType()) {
                            case LAZY:
                                LOG.debug(
                                        "Found lazy entity %s",
                                        foreignColumnType.getField().toString()
                                );
                                Object proxy = persister.createProxy(
                                        foreignColumnType.getForeignFieldClass(),
                                        foreignColumnType.getForeignDatabaseColumnType().getField().getName(),
                                        resultSetValue.getValue()
                                );

                                columnType.assign(entityInstance, proxy);
                                break;
                            case EAGER:
                                Object value = context.getEntry(
                                        foreignColumnType.getForeignFieldClass(),
                                        resultSetValue.getValue()
                                );

                                if (value == null) {
                                    String propertyName = foreignColumnType.getForeignDatabaseColumnType()
                                            .getField()
                                            .getName();

                                    SelectStatement<?> selectStatement = new SelectStatement<>(
                                            foreignColumnType.getForeignFieldClass()
                                    );

                                    selectStatement.where(new Criteria()
                                            .add(Restrictions.eq(propertyName, resultSetValue.getValue()))
                                    );

                                    value = context.getSession().uniqueResult(selectStatement);
                                }

                                columnType.assign(entityInstance, value);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Read values from result set.
     *
     * @param context target context
     * @param entityInstance target entity instance. It use for put temporary cache
     * @return read values
     * @throws SQLException any SQL exceptions
     */
    private List<ResultSetValue> readResultSet(ResultSetContext context, Object entityInstance) throws SQLException {
        List<ResultSetValue> values = new ArrayList<>();

        List<String> columnAliases = entityAliases.getColumnAliases();
        int i = 0;

        for (DatabaseColumnType columnType : metadata.getDisplayedColumnTypes()) {
            if (columnType.id()) {
                ++i;
                continue;
            }
            String currentAlias = columnAliases.get(i++);

            if (context.isResultColumn(currentAlias)) {
                Object value = columnType.dataPersister().readValue(
                        context.getDatabaseResults(),
                        currentAlias
                );

                value = ArgumentUtils.processConvertersToJavaValue(value, columnType).getValue();

                values.add(new ResultSetValue(value, context.getDatabaseResults().wasNull()));
                if (columnType.unique()) {
                    context.addEntry(value, entityInstance);
                }
            }
        }

        return values;
    }

    /**
     * Create or get entity instance.
     *
     * @param processingState target processing state
     * @return entity instance
     */
    private Object createOrGetEntity(EntityProcessingState processingState) {
        Object entityInstance;

        if (processingState.getEntityInstance() == null) {
            entityInstance = persister.instance();

            processingState.setNew(true);
            processingState.setEntityInstance(entityInstance);

            LOG.debug("Create a new instance %s", persister.getMetadata().getTableClass().getName());

            return entityInstance;
        } else {
            processingState.setNew(false);
            return processingState.getEntityInstance();
        }
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
