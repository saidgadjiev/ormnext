package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.orm.next.core.criteria.impl.Criteria;
import ru.saidgadjiev.orm.next.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.orm.next.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;

import static ru.saidgadjiev.orm.next.core.criteria.impl.Restrictions.eq;

public class CollectionQuerySpace {

    private final ForeignCollectionColumnType foreignCollectionColumnType;

    private CriteriaQuery loadCollectionQuery;

    private SimpleCriteriaQuery countOffCriteria;

    public CollectionQuerySpace(ForeignCollectionColumnType foreignCollectionColumnType) {
        this.foreignCollectionColumnType = foreignCollectionColumnType;
        loadCollectionQuery =
                new CriteriaQuery<>(foreignCollectionColumnType.getForeignFieldClass())
                        .where(new Criteria()
                                .add(eq(foreignCollectionColumnType.getForeignField().getName())));
        countOffCriteria =
                new SimpleCriteriaQuery(foreignCollectionColumnType.getForeignFieldClass())
                        .countAll()
                        .where(new Criteria()
                                .add(eq(foreignCollectionColumnType.getForeignField().getName())));
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
