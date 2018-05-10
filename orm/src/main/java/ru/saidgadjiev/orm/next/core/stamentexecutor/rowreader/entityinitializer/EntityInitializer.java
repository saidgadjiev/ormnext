package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.OrmNextProxy;
import ru.saidgadjiev.orm.next.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.internal.persister.DatabaseEntityPersister;

import java.lang.reflect.Proxy;
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

            processingState.setNew(true);
            processingState.setEntityInstance(entityInstance);
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
                columnType.assign(entityInstance, values.get(i++));
            }
            if (columnType.isForeignFieldType()) {
                switch (((ForeignColumnType) columnType).getFetchType()) {
                    case LAZY:
                        columnType.assign(entityInstance, getProxy(context.getDao(), ((ForeignColumnType) columnType).getForeignFieldClass(), values.get(i++)));
                        break;
                    case EAGER:
                        columnType.assign(entityInstance, context.getEntry(((ForeignColumnType) columnType).getForeignFieldClass(), values.get(i++)));
                        break;
                }
            }
        }
    }

    private Object getProxy(Dao dao, Class<?> entityClass, Object id) {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {entityClass}, new OrmNextProxy(dao, entityClass, id));
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
