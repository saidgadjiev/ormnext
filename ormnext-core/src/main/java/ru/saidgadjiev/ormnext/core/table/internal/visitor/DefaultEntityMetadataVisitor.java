package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
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
 * @author Said Gadjiev
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
     * Already visited column types. For avoid recursion.
     */
    private Set<DatabaseColumnType> visitedColumnTypes = new HashSet<>();

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
    public boolean start(ForeignColumnTypeImpl foreignColumnType) {
        if (!visitedColumnTypes.add(foreignColumnType)) {
            LOGGER.debug("Detected circular references for " + foreignColumnType.getField());

            return false;
        }
        if (!foreignColumnType.getFetchType().equals(FetchType.EAGER)) {
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
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);

        return true;
    }

    @Override
    public boolean start(ForeignCollectionColumnTypeImpl collectionColumnType) {
        if (!visitedColumnTypes.add(collectionColumnType)) {
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

        if (collectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            String nextUID = uidGenerator.nextUID();

            EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

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
            CollectionLoader collectionLoader = new CollectionLoader(
                    new CollectionQuerySpace(
                            new CollectionEntityAliases(
                                    foreignEntityAliases.getKeyAlias(),
                                    foreignEntityAliases.getAliasByColumnName(
                                            collectionColumnType.getForeignColumnName()
                                    )
                            ),
                            ownerMetaData.getPrimaryKeyColumnType(),
                            foreignMetaData.getPrimaryKeyColumnType(),
                            collectionColumnType
                    )
            );

            collectionInitializers.add(
                    new CollectionInitializer(
                            parentUidStack.peek(),
                            collectionLoader
                    )
            );
            parentUidStack.push(nextUID);
            foreignMetaData.accept(this);

            return true;
        } else {
            CollectionLoader collectionLoader = new CollectionLoader(
                    new CollectionQuerySpace(
                            new CollectionEntityAliases(
                                    ownerAliases.getKeyAlias(),
                                    ownerAliases.getKeyAlias()
                            ),
                            ownerMetaData.getPrimaryKeyColumnType(),
                            foreignMetaData.getPrimaryKeyColumnType(),
                            collectionColumnType
                    )
            );

            collectionInitializers.add(
                    new CollectionInitializer(
                            parentUidStack.peek(),
                            collectionLoader
                    )
            );

            return false;
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
