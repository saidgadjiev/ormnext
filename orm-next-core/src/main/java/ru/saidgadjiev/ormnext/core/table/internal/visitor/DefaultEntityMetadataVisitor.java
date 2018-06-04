package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ColumnKey;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.CollectionInitializer;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityInitializer;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.space.CollectionQuerySpace;
import ru.saidgadjiev.ormnext.core.query.space.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliasResolverContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.util.*;

/**
 * This visitor implementation use for create entity query space.
 *
 * @author said gadjiev
 */
public class DefaultEntityMetadataVisitor implements EntityMetadataVisitor {

    /**
     * Logger.
     */
    private static final Log LOGGER = LoggerFactory.getLogger(DefaultEntityMetadataVisitor.class);

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
     * @see EntityInitializer
     */
    private Map<String, EntityInitializer> entityInitializerMap = new LinkedHashMap<>();

    /**
     * Collection initializer list.
     *
     * @see CollectionInitializer
     */
    private List<CollectionInitializer> collectionInitializers = new ArrayList<>();

    /**
     * Already visited column keys. For avoid recursion.
     */
    private Set<ColumnKey> visitedColumnKey = new HashSet<>();

    /**
     * Uid stack. Stack use for retrieve column owner aliases from {@link #entityAliasResolverContext}.
     */
    private Stack<String> parentUidStack = new Stack<>();

    /**
     * Entity query space.
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
                                        EntityInitializer rootInitializer) {
        this.entityAliasResolverContext = entityAliasResolverContext;
        this.uidGenerator = uidGenerator;
        this.metaModel = metaModel;
        this.entityQuerySpace = new EntityQuerySpace(rootEntityMetadata, rootInitializer.getEntityAliases());

        entityQuerySpace.appendSelectColumns(rootInitializer.getEntityAliases(), rootEntityMetadata);
        parentUidStack.push(rootInitializer.getUid());
    }

    @Override
    public boolean start(ForeignColumnType foreignColumnType) {
        if (visitedColumnKey.contains(foreignColumnType.getColumnKey())) {
            LOGGER.debug("Detected circular references for " + foreignColumnType.getField());

            return false;
        }
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(
                foreignColumnType.getForeignFieldClass()
        ).getMetadata();
        String nextUID = uidGenerator.nextUID();
        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        entityQuerySpace.appendJoin(foreignColumnType, ownerAliases, foreignEntityAliases);
        entityQuerySpace.appendSelectColumns(foreignEntityAliases, foreignMetaData);

        entityInitializerMap.put(nextUID, new EntityInitializer(
                nextUID,
                foreignEntityAliases,
                metaModel.getPersister(foreignMetaData.getTableClass())
        ));
        visitedColumnKey.add(foreignColumnType.getColumnKey());
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);

        return true;
    }

    @Override
    public boolean start(ForeignCollectionColumnType collectionColumnType) {
        if (visitedColumnKey.contains(collectionColumnType.getColumnKey())) {
            LOGGER.debug("Detected circular references for " + collectionColumnType.getField());

            return false;
        }
        DatabaseEntityMetadata<?> ownerMetaData = metaModel.getPersister(
                collectionColumnType.getField().getDeclaringClass()
        ).getMetadata();
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(
                collectionColumnType.getCollectionObjectClass()
        ).getMetadata();
        String nextUID = uidGenerator.nextUID();

        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            entityQuerySpace.appendCollectionJoin(
                    ownerMetaData.getPrimaryKeyColumnType().columnName(),
                    collectionColumnType,
                    ownerAliases,
                    foreignEntityAliases
            );
            entityQuerySpace.appendSelectColumns(foreignEntityAliases, foreignMetaData);
            entityInitializerMap.put(nextUID, new EntityInitializer(
                    nextUID,
                    foreignEntityAliases,
                    metaModel.getPersister(foreignMetaData.getTableClass())
            ));
        }
        CollectionLoader collectionLoader = new CollectionLoader(
                new CollectionQuerySpace(
                        new CollectionEntityAliases(foreignEntityAliases.getKeyAlias()),
                        ownerMetaData.getPrimaryKeyColumnType(),
                        collectionColumnType
                )
        );

        collectionInitializers.add(
                new CollectionInitializer(
                        parentUidStack.peek(),
                        collectionLoader
                )
        );
        visitedColumnKey.add(collectionColumnType.getColumnKey());
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);


        return true;
    }

    @Override
    public void finish(ForeignColumnType foreignColumnType) {
        parentUidStack.pop();
    }

    @Override
    public void finish(ForeignCollectionColumnType foreignCollectionColumnType) {
        parentUidStack.pop();
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
    public List<EntityInitializer> getEntityInitializers() {
        return new ArrayList<>(entityInitializerMap.values());
    }

    /**
     * Return built collection initializers.
     *
     * @return built collection initializers
     */
    public List<CollectionInitializer> getCollectionInitializers() {
        return collectionInitializers;
    }

    /**
     * Return built entity query space.
     *
     * @return built entity query space
     */
    public EntityQuerySpace getEntityQuerySpace() {
        return entityQuerySpace;
    }
}
