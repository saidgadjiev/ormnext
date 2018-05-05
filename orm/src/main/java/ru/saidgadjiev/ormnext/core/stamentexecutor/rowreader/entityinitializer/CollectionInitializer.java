package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.ormnext.core.stamentexecutor.object.collection.LazyList;
import ru.saidgadjiev.ormnext.core.stamentexecutor.object.collection.LazySet;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class CollectionInitializer  {

    private static final Log LOG = LoggerFactory.getLogger(CollectionInitializer.class);

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
            Field field = foreignCollectionColumnType.getField();

            LOG.debug("Found lazy collection " + field.getDeclaringClass().getName() + " " + field.getName());

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
