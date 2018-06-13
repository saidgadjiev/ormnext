package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;

import static ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions.eq;

/**
 * Collection query space.
 *
 * @author Said Gadjiev
 */
public class CollectionQuerySpace {

    /**
     * Collection entity aliases.
     *
     * @see CollectionEntityAliases
     */
    private final CollectionEntityAliases collectionEntityAliases;

    /**
     * Collection owner primary key.
     *
     * @see DatabaseColumnType
     */
    private final DatabaseColumnType ownerPrimaryKey;

    /**
     * Collection object primary key.
     */
    private final DatabaseColumnType collectionObjectPrimaryKey;

    /**
     * Target foreign collection type.
     *
     * @see ForeignCollectionColumnTypeImpl
     */
    private final ForeignCollectionColumnTypeImpl foreignCollectionColumnType;

    /**
     * SelectQuery collection items statement.
     */
    private final SelectStatement loadCollectionQuery;

    /**
     * SelectQuery collection items count statement.
     */
    private final SelectStatement countOffCriteria;

    /**
     * Create a new query space.
     *
     * @param collectionEntityAliases     collection entity aliases
     * @param ownerPrimaryKey             owner primary key
     * @param collectionObjectPrimaryKey  collection object primary key
     * @param foreignCollectionColumnType target collection type
     */
    public CollectionQuerySpace(CollectionEntityAliases collectionEntityAliases,
                                DatabaseColumnType ownerPrimaryKey,
                                DatabaseColumnType collectionObjectPrimaryKey,
                                ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {
        this.collectionEntityAliases = collectionEntityAliases;
        this.ownerPrimaryKey = ownerPrimaryKey;
        this.collectionObjectPrimaryKey = collectionObjectPrimaryKey;
        this.foreignCollectionColumnType = foreignCollectionColumnType;

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
    }

    /**
     * Return select collection items statement.
     *
     * @return select collection items statement
     */
    public SelectStatement getLoadCollectionQuery() {
        return loadCollectionQuery;
    }

    /**
     * Return select collection items count statement.
     *
     * @return select collection items count statement
     */
    public SelectStatement getCountOffCriteria() {
        return countOffCriteria;
    }

    /**
     * Return collection column type.
     *
     * @return collection column type
     */
    public ForeignCollectionColumnTypeImpl getForeignCollectionColumnType() {
        return foreignCollectionColumnType;
    }

    /**
     * Return collection entity aliases.
     *
     * @return collection entity aliases
     */
    public CollectionEntityAliases getCollectionEntityAliases() {
        return collectionEntityAliases;
    }

    /**
     * Return owner primary key.
     *
     * @return owner primary key
     */
    public DatabaseColumnType getOwnerPrimaryKey() {
        return ownerPrimaryKey;
    }

    /**
     * Collection object primary key.
     *
     * @return collection object primary key
     */
    public DatabaseColumnType getCollectionObjectPrimaryKey() {
        return collectionObjectPrimaryKey;
    }
}
