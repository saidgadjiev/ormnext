package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.table.internal.alias.CollectionEntityAliases;

import java.sql.SQLException;

public class CollectionInitializer  {

    private String uid;

    private final CollectionEntityAliases aliases;

    private ForeignCollectionColumnType foreignCollectionColumnType;

    public CollectionInitializer(String ownerUID, CollectionEntityAliases aliases, ForeignCollectionColumnType foreignCollectionColumnType) {
        this.uid = ownerUID;
        this.aliases = aliases;
        this.foreignCollectionColumnType = foreignCollectionColumnType;
    }

    public void loadCollection(ResultSetContext resultSetContext) throws SQLException {
        Object id = resultSetContext.getDatabaseResults().getObject(aliases.getCollectionOwnerAlias());

        if (id == null) {
            return;
        }

        Object collectionObjectId = resultSetContext.getDatabaseResults().getObject(aliases.getCollectionColumnKeyAlias());

        if (collectionObjectId == null) {
            return;
        }
        ResultSetContext.EntityProcessingState processingState = resultSetContext.getProcessingState(uid, id);

        foreignCollectionColumnType.add(processingState.getEntityInstance(), resultSetContext.getSession().queryForId(aliases.getCollectionObjectClass(), collectionObjectId));
    }
}
