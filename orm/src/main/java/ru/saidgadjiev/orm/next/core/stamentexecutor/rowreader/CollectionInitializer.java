package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.CollectionEntityAliases;

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
        ResultSetContext.EntityProcessingState processingState = resultSetContext.getProcessingState(uid, id);
        Object collectionObjectId = resultSetContext.getDatabaseResults().getObject(aliases.getCollectionColumnKeyAlias());

        foreignCollectionColumnType.add(processingState.getEntityInstance(), resultSetContext.getSession().queryForId(aliases.getCollectionObjectClass(), collectionObjectId));
    }
}
