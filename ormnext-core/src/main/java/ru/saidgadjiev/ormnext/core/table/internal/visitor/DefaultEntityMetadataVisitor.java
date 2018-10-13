package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.CollectionContext;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityContext;
import ru.saidgadjiev.ormnext.core.query.space.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliasResolverContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;
import java.util.*;

/**
 * This visitor implementation use for create entity executeQuery space.
 *
 * @author Said Gadjiev
 */
public class DefaultEntityMetadataVisitor implements EntityMetadataVisitor {

    /**
     * Meta model.
     *
     * @see MetaModel
     */
    private MetaModel metaModel;

    /**
     * Alias context.
     *
     * @see EntityAliasResolverContext
     */
    private EntityAliasResolverContext entityAliasResolverContext;

    /**
     * Unique uid generator.
     * Use for generate unique uid which associated with alias group in {@link #entityAliasResolverContext}
     *
     * @see UIDGenerator
     */
    private UIDGenerator uidGenerator;

    /**
     * Entity initializer map.
     * Map associate uid with initializer.
     *
     * @see EntityContext
     */
    private Map<String, EntityContext> entityInitializerMap = new LinkedHashMap<>();

    /**
     * Collection initializer list.
     *
     * @see CollectionContext
     */
    private List<CollectionContext> collectionContexts = new ArrayList<>();

    /**
     * Already visited column types. For avoid recursion.
     */
    private Set<String> visitedFields = new HashSet<>();

    /**
     * Uid stack. Stack use for retrieve column owner aliases from {@link #entityAliasResolverContext}.
     */
    private Stack<String> parentUidStack = new Stack<>();

    /**
     * Entity executeQuery space.
     *
     * @see EntityQuerySpace
     */
    private EntityQuerySpace entityQuerySpace;

    /**
     * Create a new instance.
     *
     * @param rootEntityMetadata         target root entity meta data
     * @param metaModel                  target  meta model
     * @param entityAliasResolverContext target alias context
     * @param uidGenerator               target uid generator
     * @param rootInitializer            target initilizer for root entity
     */
    public DefaultEntityMetadataVisitor(DatabaseEntityMetadata rootEntityMetadata,
                                        MetaModel metaModel,
                                        EntityAliasResolverContext entityAliasResolverContext,
                                        UIDGenerator uidGenerator,
                                        EntityContext rootInitializer) {
        this.entityAliasResolverContext = entityAliasResolverContext;
        this.uidGenerator = uidGenerator;
        this.metaModel = metaModel;
        this.entityQuerySpace = new EntityQuerySpace(rootEntityMetadata, rootInitializer.getEntityAliases());

        entityQuerySpace.appendSelectColumns(rootInitializer.getEntityAliases(), rootEntityMetadata);
        parentUidStack.push(rootInitializer.getUid());
    }

    @Override
    public boolean start(ForeignColumnTypeImpl foreignColumnType) throws SQLException {
        if (!visitedFields.add(foreignColumnType.getField().toString())
                || !foreignColumnType.getFetchType().equals(FetchType.EAGER)) {
            return false;
        }
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getMetadata(
                foreignColumnType.getForeignFieldClass()
        );

        String nextUID = uidGenerator.nextUID();
        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        entityQuerySpace.appendJoin(foreignColumnType, ownerAliases, foreignEntityAliases);
        entityQuerySpace.appendSelectColumns(foreignEntityAliases, foreignMetaData);

        entityInitializerMap.put(nextUID, new EntityContext(
                nextUID,
                foreignEntityAliases,
                metaModel.getPersister(foreignMetaData.getTableClass())
        ));
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);

        return true;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl collectionColumnType) throws SQLException {
        if (collectionColumnType.getCollectionObjectClass()
                .equals(collectionColumnType.getField().getDeclaringClass())) {
            if (!visitedFields.add(collectionColumnType.getField().toString())) {
                return false;
            }
            if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
                startEagerCollection(collectionColumnType);
                finish(collectionColumnType);
                finish(collectionColumnType);
                startEagerCollection(collectionColumnType);

                return true;
            } else {
                startLazyCollection(collectionColumnType);
                finish(collectionColumnType);
                startLazyCollection(collectionColumnType);

                return false;
            }
        } else if (!visitedFields.add(collectionColumnType.getField().toString())) {
            return false;
        }

        if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            return startEagerCollection(collectionColumnType);
        } else {
            return startLazyCollection(collectionColumnType);
        }
    }

    @Override
    public void finish(ForeignColumnTypeImpl foreignColumnType) {
        parentUidStack.pop();
    }

    @Override
    public void finish(ForeignCollectionColumnTypeImpl foreignCollectionColumnType) {
        parentUidStack.pop();
    }

    @Override
    public boolean start(SimpleDatabaseColumnTypeImpl databaseColumnType) {
        return false;
    }

    @Override
    public void finish(SimpleDatabaseColumnTypeImpl databaseColumnType) {

    }

    @Override
    public void finish(DatabaseEntityMetadata<?> entityMetadata) {

    }

    @Override
    public boolean start(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        return true;
    }

    /**
     * Return built initializers.
     *
     * @return built initializers
     */
    public List<EntityContext> getEntityInitializers() {
        return new ArrayList<>(entityInitializerMap.values());
    }

    /**
     * Return built collection initializers.
     *
     * @return built collection initializers
     */
    public List<CollectionContext> getCollectionContexts() {
        return collectionContexts;
    }

    /**
     * Return built entity executeQuery space.
     *
     * @return built entity executeQuery space
     */
    public EntityQuerySpace getEntityQuerySpace() {
        return entityQuerySpace;
    }

    /**
     * Append eager collection join.
     *
     * @param collectionColumnType target column type
     * @return true if handle success
     * @throws SQLException any exceptions
     */
    private boolean startEagerCollection(ForeignCollectionColumnTypeImpl collectionColumnType) throws SQLException {
        DatabaseEntityMetadata<?> ownerMetaData = metaModel.getMetadata(
                collectionColumnType.getField().getDeclaringClass()
        );
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(
                collectionColumnType.getCollectionObjectClass()
        ).getMetadata();
        String nextUID = uidGenerator.nextUID();

        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        entityQuerySpace.appendCollectionJoin(
                collectionColumnType,
                ownerAliases,
                foreignEntityAliases
        );
        entityQuerySpace.appendSelectColumns(foreignEntityAliases, foreignMetaData);
        entityInitializerMap.put(nextUID, new EntityContext(
                nextUID,
                foreignEntityAliases,
                metaModel.getPersister(foreignMetaData.getTableClass())
        ));

        CollectionLoader collectionLoader = new CollectionLoader(collectionColumnType);

        collectionContexts.add(
                new CollectionContext(
                        parentUidStack.peek(),
                        new CollectionEntityAliases(
                                foreignEntityAliases.getKeyAlias(),
                                ownerAliases.getKeyAlias()
                        ),
                        collectionLoader,
                        ownerMetaData
                )
        );
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);

        return true;
    }

    /**
     * Append lazy collection join.
     *
     * @param collectionColumnType target column type
     * @return true if handle success
     */
    private boolean startLazyCollection(ForeignCollectionColumnTypeImpl collectionColumnType) {
        DatabaseEntityMetadata<?> ownerMetaData = metaModel.getMetadata(
                collectionColumnType.getField().getDeclaringClass()
        );
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        CollectionLoader collectionLoader = new CollectionLoader(collectionColumnType);

        collectionContexts.add(
                new CollectionContext(
                        parentUidStack.peek(),
                        new CollectionEntityAliases(
                                ownerAliases.getAliasByColumnName(
                                        collectionColumnType.getForeignColumnType().getForeignColumnName()
                                ),
                                ownerAliases.getKeyAlias()
                        ),
                        collectionLoader,
                        ownerMetaData
                )
        );

        return false;
    }
}
