package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.collection.LazyList;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.collection.LazySet;
import ru.saidgadjiev.orm.next.core.table.internal.alias.CollectionEntityAliases;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class CollectionInitializer  {

    private String uid;

    private final CollectionEntityAliases aliases;

    private CollectionLoader collectionLoader;

    public CollectionInitializer(String ownerUID,
                                 CollectionEntityAliases aliases,
                                 CollectionLoader collectionLoader) {
        this.uid = ownerUID;
        this.aliases = aliases;
        this.collectionLoader = collectionLoader;
    }

    public void loadCollection(ResultSetContext resultSetContext) throws SQLException {
        Object id = resultSetContext.getDatabaseResults().getObject(aliases.getOwnerAliases().getKeyAlias());

        if (id == null) {
            return;
        }
        ResultSetContext.EntityProcessingState processingState = resultSetContext.getProcessingState(uid, id);
        Object instance = processingState.getEntityInstance();
        ForeignCollectionColumnType foreignCollectionColumnType = collectionLoader.getGoreignCollectionColumnType();

        if (foreignCollectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            Object collectionObjectId = resultSetContext.getDatabaseResults().getObject(aliases.getCollectionColumnKeyAlias());

            if (collectionObjectId == null) {
                return;
            }

            foreignCollectionColumnType.add(instance, resultSetContext.getEntry(aliases.getCollectionObjectClass(), collectionObjectId));
        } else {
            try {
                switch (foreignCollectionColumnType.getCollectionType()) {
                    case LIST:
                        foreignCollectionColumnType.assign(instance, new LazyList(
                                collectionLoader,
                                resultSetContext.getDao(),
                                id,
                                (List) foreignCollectionColumnType.access(instance))
                        );
                        break;
                    case SET:
                        foreignCollectionColumnType.assign(instance, new LazySet(
                                collectionLoader,
                                resultSetContext.getDao(),
                                id,
                                (Set) foreignCollectionColumnType.access(instance))
                        );
                        break;
                    case UNDEFINED:
                        throw new RuntimeException("Unknown collection type " + foreignCollectionColumnType.getField().getType());
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }
}
