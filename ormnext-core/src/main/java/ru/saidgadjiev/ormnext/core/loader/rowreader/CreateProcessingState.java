package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;

import java.sql.SQLException;
import java.util.Map;

import static ru.saidgadjiev.ormnext.core.loader.ResultSetContext.EntityProcessingState;

/**
 * Created by said on 23.07.2018.
 */
public class CreateProcessingState implements EntityMetadataVisitor {

    private ResultSetContext resultSetContext;

    private String uid;

    private DatabaseEntityPersister persister;

    private EntityAliases aliases;

    CreateProcessingState(EntityContext entityContext,
                          ResultSetContext resultSetContext) {
        this.resultSetContext = resultSetContext;
        this.persister = entityContext.getPersister();
        this.uid = entityContext.getUid();
        this.aliases = entityContext.getEntityAliases();
    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) throws SQLException {
        return createProcessingState(databaseEntityMetadata.getPrimaryKeyColumnType());
    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException {
        return false;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) throws SQLException {
        return false;
    }

    @Override
    public void finish(ForeignColumnTypeImpl foreignColumnType) {

    }

    @Override
    public void finish(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {

    }

    @Override
    public boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType) throws SQLException {
        String alias = aliases.getAliasByColumnName(databaseColumnType.columnName());

        if (databaseColumnType.unique() && resultSetContext.isResultColumn(alias)) {
            Map<String, ResultSetValue> values = resultSetContext.getCurrentRow().getValues(aliases);
            ResultSetValue idValue = values.get(aliases.getKeyAlias());
            EntityProcessingState processingState = resultSetContext.getProcessingState(uid, idValue.getValue());
            Object entityInstance = processingState.getEntityInstance();
            ResultSetValue uniqueValue = values.get(alias);

            resultSetContext.addEntry(uniqueValue.getValue(), entityInstance);
        }

        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {

    }

    private boolean createProcessingState(DatabaseColumnType idColumnType) throws SQLException {
        String idAlias = aliases.getKeyAlias();

        if (!resultSetContext.isResultColumn(idAlias)) {
            return false;
        }

        Map<String, ResultSetValue> values = resultSetContext.getCurrentRow().getValues(aliases);
        ResultSetValue idValue = values.get(idAlias);

        if (idValue.wasNull()) {
            return false;
        }

        Object id = idValue.getValue();
        EntityProcessingState processingState = resultSetContext.getOrCreateProcessingState(uid, id);
        Object entityInstance;

        if (processingState.getEntityInstance() == null) {
            entityInstance = persister.instance();

            processingState.setNew(true);
            processingState.setEntityInstance(entityInstance);
            idColumnType.assign(entityInstance, id);
            processingState.setValuesFromResultSet(values);

            resultSetContext.addEntry(id, entityInstance);
            resultSetContext.putToCache(id, entityInstance);
        } else {
            processingState.setNew(false);
        }

        return true;
    }
}
