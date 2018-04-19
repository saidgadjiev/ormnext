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
        EntityProcessingState processingState = context.getProcessingState(uid);
        Object entityInstance = persister.instance();

        processingState.setEntityInstance(entityInstance);
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        IDatabaseColumnType primaryKey = entityMetadata.getPrimaryKey();
        Object id = context.getDatabaseResults().getObject(entityAliases.getKeyAlias());

        primaryKey.assign(entityInstance, id);
        context.getSession().addEntityToCache(entityMetadata.getTableClass(), id, entityInstance);
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
        DatabaseEntityMetadata<?> entityMetadata = persister.getMetadata();
        EntityProcessingState processingState = context.getProcessingState(uid);
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
