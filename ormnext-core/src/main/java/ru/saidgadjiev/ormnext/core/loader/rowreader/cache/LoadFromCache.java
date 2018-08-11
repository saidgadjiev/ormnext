package ru.saidgadjiev.ormnext.core.loader.rowreader.cache;

import ru.saidgadjiev.ormnext.core.cache.Cache;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.object.Lazy;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.proxymaker.Proxy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions.eq;

/**
 * Visitor implementation for load references.
 *
 * @author Said Gadjiev
 */

public class LoadFromCache implements EntityMetadataVisitor {

    /**
     * Logger.
     */
    private static final Log LOG = LoggerFactory.getLogger(LoadFromCache.class);

    /**
     * Context.
     */
    private CacheObjectContext cacheObjectContext;

    /**
     * Parent object stack.
     */
    private Stack<Object> parentObjectStack = new Stack<>();

    /**
     * Cache.
     */
    private Cache cache;

    /**
     * Create a new instance.
     *
     * @param cacheObjectContext target context
     * @param current            target object
     */
    public LoadFromCache(CacheObjectContext cacheObjectContext, Object current) {
        this.cacheObjectContext = cacheObjectContext;
        this.cache = cacheObjectContext.getCache();

        parentObjectStack.push(current);
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
        Object parent = parentObjectStack.peek();

        if (foreignColumnType.getFetchType().equals(FetchType.EAGER)) {
            DatabaseEntityMetadata<?> foreignMetadata = cacheObjectContext.getMetadata(
                    foreignColumnType.getForeignFieldClass()
            );

            Object foreignObject = foreignColumnType.access(parent);

            Object foreignObjectKey = foreignMetadata.getPrimaryKeyColumnType().access(foreignObject);

            Optional<Object> cached = cache.queryForId(foreignColumnType.getForeignFieldClass(), foreignObjectKey);

            if (cached.isPresent()) {
                copy(cached.get(), foreignObject);
                LOG.debug("Reference " + foreignColumnType.getField() + " loaded from cache");
            } else {
                copy(
                        cacheObjectContext.getSession().queryForId(
                                foreignColumnType.getForeignFieldClass(),
                                foreignObjectKey
                        ),
                        foreignObject
                );
                LOG.debug("Reference " + foreignColumnType.getField() + " loaded from datasource");
            }

            parentObjectStack.push(foreignObject);

            foreignMetadata.accept(this);

            return true;
        } else {
            Proxy proxy = (Proxy) foreignColumnType.access(parent);
            Lazy lazy = (Lazy) proxy.getHandler();

            lazy.setNonInitialized();
            lazy.attach(cacheObjectContext.getSession());

            LOG.debug("Lazy reference " + foreignColumnType.getField() + " reset");

            return false;
        }
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) throws SQLException {
        Object parent = parentObjectStack.peek();

        if (foreignCollectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            DatabaseEntityMetadata<?> ownerMetadata = cacheObjectContext.getMetadata(
                    foreignCollectionColumnType.getField().getDeclaringClass()
            );

            Collection<Object> foreignCollection = foreignCollectionColumnType.access(parent);

            Object ownerKey = ownerMetadata.getPrimaryKeyColumnType().access(parent);

            SelectStatement selectStatement = cacheObjectContext
                    .getSession()
                    .statementBuilder()
                    .createSelectStatement(foreignCollectionColumnType.getCollectionObjectClass())
                    .where(new Criteria()
                    .add(eq(foreignCollectionColumnType.getForeignField().getName(), ownerKey)));

            Optional<List<Object>> cached = cache.list(selectStatement);
            List<Object> result;

            if (cached.isPresent()) {
                result = cached.get();

                LOG.debug("Collection reference " + foreignCollectionColumnType.getField() + " loaded from cache");
            } else {
                result = selectStatement.list();

                LOG.debug(
                        "Collection reference " + foreignCollectionColumnType.getField() + " loaded from datasource"
                );
            }
            foreignCollection.clear();
            foreignCollection.addAll(result);
        } else {
            Object lazy = foreignCollectionColumnType.access(parent);

            ((Lazy) lazy).setNonInitialized();
            ((Lazy) lazy).attach(cacheObjectContext.getSession());

            LOG.debug("Lazy collection " + foreignCollectionColumnType.getField() + " reset");
        }

        return false;
    }

    @Override
    public void finish(ForeignColumnTypeImpl foreignColumnType) {
        parentObjectStack.pop();
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

    /**
     * Copy values from src to target without references.
     *
     * @param src    target source object
     * @param target target object
     */
    private void copy(Object src, Object target) {
        DatabaseEntityMetadata<?> metadata = cacheObjectContext.getMetadata(src.getClass());

        for (DatabaseColumnType columnType : metadata.toDatabaseColumnTypes()) {
            if (columnType.id()) {
                continue;
            }

            Object srcValue = columnType.access(src);

            columnType.assign(target, srcValue);
        }
    }
}
