package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext.EntityProcessingState;

public class EntityInitializer {

    private static final Log LOG = LoggerFactory.getLogger(EntityInitializer.class);

    private String uid;

    private DatabaseEntityPersister persister;

    private EntityAliases entityAliases;

    public EntityInitializer(String uid, EntityAliases entityAliases, DatabaseEntityPersister persister) {
        this.uid = uid;
        this.persister = persister;
        this.entityAliases = entityAliases;
    }

    public Object startRead(ResultSetContext context) throws SQLException {
        IDatabaseColumnType idColumnType = persister.getMetadata().getPrimaryKey();
        Object id = idColumnType.getDataPersister().readValue(context.getDatabaseResults(), entityAliases.getKeyAlias());

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
        IDatabaseColumnType primaryKey = entityMetadata.getPrimaryKey();

        primaryKey.assign(entityInstance, id);

        addToCache(context, id, entityInstance);
        List<Object> values = new ArrayList<>();

        List<String> columnAliases = entityAliases.getColumnAliases();
        int i = 0;

        for (IDatabaseColumnType columnType : entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            if (columnType.isId()) {
                continue;
            }
            Object value = columnType.getDataPersister().readValue(context.getDatabaseResults(), columnAliases.get(i++));

            values.add(value);
        }
        processingState.setValuesFromResultSet(values);
        LOG.debug("Values read from resultset %s", values);

        return id;
    }

    public void finishRead(ResultSetContext context) throws SQLException {
        Map<Object, EntityProcessingState> processingStates = context.getProcessingStates(uid);

        if (processingStates != null) {
            for (Map.Entry<Object, EntityProcessingState> entry : processingStates.entrySet()) {
                finishRead(context, entry.getValue());
            }
        }
    }

    private void finishRead(ResultSetContext context, EntityProcessingState processingState) throws SQLException {
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        Object entityInstance = processingState.getEntityInstance();
        List<Object> values = processingState.getValues();
        int i = 0;

        for (IDatabaseColumnType columnType : entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            if (columnType.isId()) {
                continue;
            }
            if (columnType.isDbFieldType()) {
                Object value = values.get(i++);

                if (columnType.getConverters().isPresent()) {
                    List<Converter<?, Object>> converters = columnType.getConverters().get();

                    for (Converter converter: converters) {
                        value = converter.sqlToJava(value);
                    }
                }
                columnType.assign(entityInstance, value);
            }
            if (columnType.isForeignFieldType()) {
                ForeignColumnType foreignColumnType = (ForeignColumnType) columnType;

                switch (foreignColumnType.getFetchType()) {
                    case LAZY:
                        LOG.debug("Found lazy entity %s %s", foreignColumnType.getField().getDeclaringClass().getName(), foreignColumnType.getField().getName());
                        Object proxy = persister.createProxy(foreignColumnType.getCollectionObjectClass(), values.get(i++));

                        columnType.assign(entityInstance, proxy);
                        break;
                    case EAGER:
                        columnType.assign(entityInstance, context.getEntry(foreignColumnType.getCollectionObjectClass(), values.get(i++)));
                        break;
                }
            }
        }
    }

    private void addToCache(ResultSetContext context, Object id, Object entityInstance) {
        context.addEntry(id, entityInstance);
        context.getDao().cacheHelper().saveToCache(id, entityInstance);
    }

    public String getUid() {
        return uid;
    }

    public EntityAliases getEntityAliases() {
        return entityAliases;
    }
}
