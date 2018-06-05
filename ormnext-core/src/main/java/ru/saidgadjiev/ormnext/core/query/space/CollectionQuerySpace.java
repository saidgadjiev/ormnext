package ru.saidgadjiev.ormnext.core.query.space;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;

import static ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions.eq;

/**
 * Collection query space.
 *
 * @author said gadjiev
 */
public class CollectionQuerySpace {

    /**
     * Collection entity aliases.
     * @see CollectionEntityAliases
     */
    private final CollectionEntityAliases collectionEntityAliases;

    /**
     * Collection owner primary key.
     * @see IDatabaseColumnType
     */
    private IDatabaseColumnType ownerPrimaryKey;

    /**
     * Target foreign collection type.
     * @see ForeignCollectionColumnType
     */
    private final ForeignCollectionColumnType foreignCollectionColumnType;

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
     * @param collectionEntityAliases collection entity aliases
     * @param ownerPrimaryKey owner primary key
     * @param foreignCollectionColumnType target collection type
     */
    public CollectionQuerySpace(CollectionEntityAliases collectionEntityAliases,
                                IDatabaseColumnType ownerPrimaryKey,
                                ForeignCollectionColumnType foreignCollectionColumnType) {
        this.collectionEntityAliases = collectionEntityAliases;
        this.ownerPrimaryKey = ownerPrimaryKey;
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
     * @return select collection items statement
     */
    public SelectStatement getLoadCollectionQuery() {
        return loadCollectionQuery;
    }

    /**
     * Return select collection items count statement.
     * @return select collection items count statement
     */
    public SelectStatement getCountOffCriteria() {
        return countOffCriteria;
    }

    /**
     * Return collection column type.
     * @return collection column type
     */
    public ForeignCollectionColumnType getForeignCollectionColumnType() {
        return foreignCollectionColumnType;
    }

    /**
     * Return collection entity aliases.
     * @return collection entity aliases
     */
    public CollectionEntityAliases getCollectionEntityAliases() {
        return collectionEntityAliases;
    }

    /**
     * Return owner primary key.
     * @return owner primary key
     */
    public IDatabaseColumnType getOwnerPrimaryKey() {
        return ownerPrimaryKey;
    }
}
