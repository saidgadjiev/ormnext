package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void startRead(ResultSetContext context) throws SQLException {
        Object id = context.getDatabaseResults().getObject(entityAliases.getKeyAlias());

        if (id == null) {
            return;
        }
        EntityProcessingState processingState = context.getProcessingState(uid, id);
        Object entityInstance;

        LOG.debug("Processing " + persister.getMetadata().getTableClass().getName() + " id " + id);
        if (processingState.getEntityInstance() == null) {
            entityInstance = persister.instance();

            processingState.setNew(true);
            processingState.setEntityInstance(entityInstance);
            LOG.debug("Create new instance " + persister.getMetadata().getTableClass().getName());
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

        for (IDatabaseColumnType columnType: entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            if (columnType.isId()) {
                continue;
            }
            Object value = context.getDatabaseResults().getObject(columnAliases.get(i++));

            values.add(value);
        }
        processingState.setValuesFromResultSet(values);
        LOG.debug("Values read from resultset " + values.toString());
    }

    public void finishRead(ResultSetContext context) throws SQLException {
        Object id = context.getDatabaseResults().getObject(entityAliases.getKeyAlias());

        if (id == null) {
            return;
        }
        EntityProcessingState processingState = context.getProcessingState(uid, id);
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        Object entityInstance = processingState.getEntityInstance();
        List<Object> values = processingState.getValues();
        int  i = 0;

        for (IDatabaseColumnType columnType: entityMetadata.getFieldTypes()) {
            if (columnType.isForeignCollectionFieldType()) {
                continue;
            }
            if (columnType.isId()) {
                continue;
            }
            if (columnType.isDbFieldType()) {
                columnType.assign(entityInstance, columnType.getDataPersister().parseSqlToJava(columnType, values.get(i++)));
            }
            if (columnType.isForeignFieldType()) {
                ForeignColumnType foreignColumnType = (ForeignColumnType) columnType;

                switch (foreignColumnType.getFetchType()) {
                    case LAZY:
                        LOG.debug("Found lazy entity " + foreignColumnType.getField().getDeclaringClass().getName() + " " + foreignColumnType.getField().getName());
                        columnType.assign(entityInstance, context.getDao().createProxy(foreignColumnType.getForeignFieldClass(), values.get(i++)));
                        break;
                    case EAGER:
                        columnType.assign(entityInstance, context.getEntry(foreignColumnType.getForeignFieldClass(), values.get(i++)));
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
