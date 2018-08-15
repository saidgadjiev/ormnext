package ru.saidgadjiev.ormnext.core.loader.rowreader.cache;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazyList;
import ru.saidgadjiev.ormnext.core.loader.object.collection.LazySet;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by said on 12.08.2018.
 */
public class PrepareForCache implements EntityMetadataVisitor {

    private CacheObjectContext cacheObjectContext;

    private Object current;

    public PrepareForCache(Object current, CacheObjectContext cacheObjectContext) {
        this.current = current;
        this.cacheObjectContext = cacheObjectContext;
    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) throws SQLException {
        return true;
    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {

    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException {
        if (foreignColumnType.getFetchType().equals(FetchType.LAZY)) {
            DatabaseEntityMetadata<?> foreignMetaData = cacheObjectContext.getMetadata(
                    foreignColumnType.getForeignFieldClass()
            );

            Object foreignObject = foreignColumnType.access(current);

            if (foreignObject == null) {
                return false;
            }
            Object foreignKey = foreignMetaData.getPrimaryKeyColumnType().access(foreignObject);

            Object proxy = cacheObjectContext.getPersister(foreignColumnType.getForeignFieldClass()).createProxy(
                    cacheObjectContext.getSession(),
                    foreignColumnType.getForeignFieldClass(),
                    foreignColumnType.getForeignDatabaseColumnType().getField().getName(),
                    foreignKey
            );

            foreignColumnType.assign(current, proxy);
        }

        return false;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) throws SQLException {
        if (foreignCollectionColumnType.getFetchType().equals(FetchType.LAZY)) {
            Collection<Object> collection = foreignCollectionColumnType.access(current);

            if (collection == null) {
                return false;
            }
            DatabaseEntityMetadata<?> parentMetadata = cacheObjectContext.getMetadata(
                    current.getClass()
            );
            CollectionLoader collectionLoader = new CollectionLoader(foreignCollectionColumnType);

            switch (foreignCollectionColumnType.getCollectionType()) {
                case LIST:
                    foreignCollectionColumnType.assign(current, new LazyList(
                            collectionLoader,
                            cacheObjectContext.getSession(),
                            parentMetadata.getPrimaryKeyColumnType().access(current),
                            (List) collection
                    ));
                    break;
                case SET:
                    foreignCollectionColumnType.assign(current, new LazySet(
                            collectionLoader,
                            cacheObjectContext.getSession(),
                            parentMetadata.getPrimaryKeyColumnType().access(current),
                            (Set) collection
                    ));
                    break;
                default:
                    throw new RuntimeException(
                            "Unknown collection type " + foreignCollectionColumnType.getField().getType()
                    );
            }
        }

        return false;
    }

    @Override
    public void finish(ForeignColumnTypeImpl foreignColumnType) {

    }

    @Override
    public void finish(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {

    }

    @Override
    public boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType) throws SQLException {
        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }
}
