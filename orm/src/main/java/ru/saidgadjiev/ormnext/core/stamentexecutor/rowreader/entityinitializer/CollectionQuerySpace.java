package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;


public class CollectionQuerySpace {

    private final CollectionEntityAliases collectionEntityAliases;

    private IDatabaseColumnType ownerPrimaryKey;

    private final ForeignCollectionColumnType foreignCollectionColumnType;

    private CriteriaQuery loadCollectionQuery;

    private CriteriaQuery countOffCriteria;

    public CollectionQuerySpace(CollectionEntityAliases collectionEntityAliases, IDatabaseColumnType ownerPrimaryKey, ForeignCollectionColumnType foreignCollectionColumnType) {
        this.collectionEntityAliases = collectionEntityAliases;
        this.ownerPrimaryKey = ownerPrimaryKey;
        this.foreignCollectionColumnType = foreignCollectionColumnType;

        loadCollectionQuery =
                new CriteriaQuery<>(foreignCollectionColumnType.getCollectionObjectClass())
                        .where(new Criteria()
                                .add(Restrictions.eq(foreignCollectionColumnType.getForeignField().getName(), null)));
        countOffCriteria =
                new CriteriaQuery<>(foreignCollectionColumnType.getCollectionObjectClass())
                        .withoutJoins(true)
                        .countOff()
                        .where(new Criteria()
                                .add(Restrictions.eq(foreignCollectionColumnType.getForeignField().getName(), null)));
    }

    public CriteriaQuery getLoadCollectionQuery() {
        return loadCollectionQuery;
    }

    public CriteriaQuery getCountOffCriteria() {
        return countOffCriteria;
    }

    public ForeignCollectionColumnType getForeignCollectionColumnType() {
        return foreignCollectionColumnType;
    }

    public CollectionEntityAliases getCollectionEntityAliases() {
        return collectionEntityAliases;
    }

    public IDatabaseColumnType getOwnerPrimaryKey() {
        return ownerPrimaryKey;
    }
}
