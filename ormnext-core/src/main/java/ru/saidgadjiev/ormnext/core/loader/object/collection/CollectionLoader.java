package ru.saidgadjiev.ormnext.core.loader.object.collection;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;

import java.sql.SQLException;
import java.util.List;

import static ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions.eq;

/**
 * Collection loader.
 *
 * @author Said Gadjiev
 */
public class CollectionLoader {

    /**
     * SelectQuery collection items statement.
     */
    private final SelectStatement loadCollectionQuery;

    /**
     * SelectQuery collection items count statement.
     */
    private final SelectStatement<?> countOffCriteria;

    /**
     * Collection column type.
     */
    private ForeignCollectionColumnTypeImpl foreignCollectionColumnType;

    /**
     * Create a new loader.
     *
     * @param foreignCollectionColumnType target collection column type
     */
    public CollectionLoader(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {

        this.loadCollectionQuery =
                new SelectStatement<>(foreignCollectionColumnType.getCollectionObjectClass())
                        .where(new Criteria()
                                .add(eq(foreignCollectionColumnType.getForeignField().getName(), null)));
        this.countOffCriteria =
                new SelectStatement<>(foreignCollectionColumnType.getCollectionObjectClass())
                        .withoutJoins(true)
                        .countOff()
                        .where(new Criteria()
                                .add(eq(foreignCollectionColumnType.getForeignField().getName(), null)));
        this.foreignCollectionColumnType = foreignCollectionColumnType;
    }

    /**
     * Load collection.
     * @param session sesion
     * @param id collection owner object id
     * @return loaded collection
     * @throws SQLException any SQL exceptions
     */
    public List<Object> loadCollection(Session session, Object id) throws SQLException {
        return session.list(loadCollectionQuery.setObject(1, id));
    }

    /**
     * Retrieve collection size.
     * @param session session
     * @param id collection owner object id
     * @return collection size
     * @throws SQLException any SQL exceptions
     */
    public long loadSize(Session session, Object id) throws SQLException {
        return session.queryForLong(countOffCriteria.setObject(1, id));
    }

    /**
     * Return current collection column type.
     *
     * @return collection column type
     */
    public ForeignCollectionColumnTypeImpl getForeignCollectionColumnType() {
        return foreignCollectionColumnType;
    }

    /**
     * Return collection load query.
     *
     * @return collection load query
     */
    public SelectStatement<?> getLoadCollectionQuery() {
        return loadCollectionQuery;
    }
}
