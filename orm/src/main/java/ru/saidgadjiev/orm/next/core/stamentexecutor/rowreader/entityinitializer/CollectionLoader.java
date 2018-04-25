package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;

import java.sql.SQLException;
import java.util.List;

public class CollectionLoader {

    private CollectionQuerySpace collectionQuerySpace;

    public CollectionLoader(CollectionQuerySpace collectionQuerySpace) {
        this.collectionQuerySpace = collectionQuerySpace;
    }

    public List<Object> loadCollection(Dao dao, Object id) throws SQLException {
        return dao.list(collectionQuerySpace.getLoadCollectionQuery().addArg(1, id));
    }

    public long loadSize(Dao dao, Object id) throws SQLException {
        return dao.queryForLong(collectionQuerySpace.getCountOffCriteria().addArg(1, id));
    }

    public ForeignCollectionColumnType getGoreignCollectionColumnType() {
        return collectionQuerySpace.getForeignCollectionColumnType();
    }
}
