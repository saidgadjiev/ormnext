package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;

import java.sql.SQLException;
import java.util.List;

public class CollectionLoader {

    private CollectionQuerySpace collectionQuerySpace;

    public CollectionLoader(CollectionQuerySpace collectionQuerySpace) {
        this.collectionQuerySpace = collectionQuerySpace;
    }

    public List<Object> loadCollection(Session session, Object id) throws SQLException {
        return session.list(collectionQuerySpace.getLoadCollectionQuery().addArg(1, id));
    }

    public long loadSize(Session session, Object id) throws SQLException {
        return session.queryForLong(collectionQuerySpace.getCountOffCriteria().addArg(1, id));
    }

    public ForeignCollectionColumnType getGoreignCollectionColumnType() {
        return collectionQuerySpace.getForeignCollectionColumnType();
    }

    public CollectionEntityAliases getCollectionEntityAliases() {
        return collectionQuerySpace.getCollectionEntityAliases();
    }

    public DataPersister<?> getOwnerPrimaryKeyPersister() {
        return collectionQuerySpace.getOwnerPrimaryKey().getDataPersister();
    }
}
