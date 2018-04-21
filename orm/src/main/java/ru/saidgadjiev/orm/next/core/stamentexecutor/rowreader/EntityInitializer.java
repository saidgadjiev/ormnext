package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.persister.DatabaseEntityPersister;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext.EntityProcessingState;

public class EntityInitializer {

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

        if (processingState.getEntityInstance() == null) {
            entityInstance = persister.instance();

            processingState.setEntityKey(id);
            processingState.setEntityInstance(entityInstance);
        } else {
            entityInstance = processingState.getEntityInstance();
        }
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        IDatabaseColumnType primaryKey = entityMetadata.getPrimaryKey();

        primaryKey.assign(entityInstance, id);
        context.getSession().cacheHelper().saveToCache(entityInstance, id);
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
    }

    public void finishRead(ResultSetContext context) throws SQLException {
        Object id = context.getDatabaseResults().getObject(entityAliases.getKeyAlias());
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
                columnType.assign(entityInstance, values.get(i++));
            }
            if (columnType.isForeignFieldType()) {
                columnType.assign(entityInstance, context.getSession().queryForId(((ForeignColumnType) columnType).getForeignFieldClass(), values.get(i++)));
            }
        }
    }

    public String getUid() {
        return uid;
    }

    public EntityAliases getEntityAliases() {
        return entityAliases;
    }
}
