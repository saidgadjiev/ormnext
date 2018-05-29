package ru.saidgadjiev.ormnext.core.loader.object.collection;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.field.data_persister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.field_type.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.query.space.CollectionQuerySpace;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;

import java.sql.SQLException;
import java.util.List;

/**
 * Collection loader.
 *
 * @author said gadjiev
 */
public class CollectionLoader {

    /**
     * Collection query space.
     * @see CollectionQuerySpace
     */
    private CollectionQuerySpace collectionQuerySpace;

    /**
     * Create a new loader.
     * @param collectionQuerySpace collection entity query space
     */
    public CollectionLoader(CollectionQuerySpace collectionQuerySpace) {
        this.collectionQuerySpace = collectionQuerySpace;
    }

    /**
     * Load collection.
     * @param session sesion
     * @param id collection owner object id
     * @return loaded collection
     * @throws SQLException any SQL exceptions
     */
    public List<Object> loadCollection(Session session, Object id) throws SQLException {
        return session.list(collectionQuerySpace.getLoadCollectionQuery().setObject(1, id));
    }

    /**
     * Retrieve collection size.
     * @param session session
     * @param id collection owner object id
     * @return collection size
     * @throws SQLException any SQL exceptions
     */
    public long loadSize(Session session, Object id) throws SQLException {
        return session.queryForLong(collectionQuerySpace.getCountOffCriteria().setObject(1, id));
    }

    /**
     * Return current collection column type.
     * @return current collection column type
     */
    public ForeignCollectionColumnType getGoreignCollectionColumnType() {
        return collectionQuerySpace.getForeignCollectionColumnType();
    }

    /**
     * Return collection entity aliases.
     * @return collection entity aliases
     */
    public CollectionEntityAliases getCollectionEntityAliases() {
        return collectionQuerySpace.getCollectionEntityAliases();
    }

    /**
     * Return owner primary key persister.
     * @return owner primary key persister
     */
    public DataPersister getOwnerPrimaryKeyPersister() {
        return collectionQuerySpace.getOwnerPrimaryKey().getDataPersister();
    }
}
