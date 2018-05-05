package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;


public class CollectionQuerySpace {

    private final ForeignCollectionColumnType foreignCollectionColumnType;

    private CriteriaQuery loadCollectionQuery;

    private SimpleCriteriaQuery countOffCriteria;

    public CollectionQuerySpace(ForeignCollectionColumnType foreignCollectionColumnType) {
        this.foreignCollectionColumnType = foreignCollectionColumnType;
        loadCollectionQuery =
                new CriteriaQuery<>(foreignCollectionColumnType.getForeignFieldClass())
                        .where(new Criteria()
                                .add(Restrictions.eq(foreignCollectionColumnType.getForeignField().getName())));
        countOffCriteria =
                new SimpleCriteriaQuery(foreignCollectionColumnType.getForeignFieldClass())
                        .countAll()
                        .where(new Criteria()
                                .add(Restrictions.eq(foreignCollectionColumnType.getForeignField().getName())));
    }

    public CriteriaQuery getLoadCollectionQuery() {
        return loadCollectionQuery;
    }

    public SimpleCriteriaQuery getCountOffCriteria() {
        return countOffCriteria;
    }

    public ForeignCollectionColumnType getForeignCollectionColumnType() {
        return foreignCollectionColumnType;
    }
}
