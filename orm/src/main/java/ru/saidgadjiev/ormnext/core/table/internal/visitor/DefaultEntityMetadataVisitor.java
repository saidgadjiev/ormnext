package ru.saidgadjiev.ormnext.core.table.internal.visitor;

import ru.saidgadjiev.ormnext.core.common.UIDGenerator;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnKey;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionInitializer;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionQuerySpace;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.EntityInitializer;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliasResolverContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.queryspace.EntityQuerySpace;

import java.util.*;

public class DefaultEntityMetadataVisitor implements EntityMetadataVisitor {

    private final static Log LOGGER = LoggerFactory.getLogger(DefaultEntityMetadataVisitor.class);

    private MetaModel metaModel;

    private EntityAliasResolverContext entityAliasResolverContext;

    private UIDGenerator uidGenerator;

    private Map<String, EntityInitializer> entityInitializerMap = new LinkedHashMap<>();

    private List<CollectionInitializer> collectionInitializers = new ArrayList<>();

    private Set<ForeignColumnKey> visitedForeignColumnKey = new HashSet<>();

    private Stack<String> parentUidStack = new Stack<>();

    private EntityQuerySpace entityQuerySpace;

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

    public void visit(ForeignColumnType foreignColumnType) {
        if (visitedForeignColumnKey.contains(foreignColumnType.getForeignColumnKey())) {
            LOGGER.debug("Detected circular references!");
            return;
        }
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(foreignColumnType.getCollectionObjectClass()).getMetadata();
        String nextUID = uidGenerator.nextUID();
        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        entityQuerySpace.appendJoin(foreignColumnType, ownerAliases, foreignEntityAliases);
        entityQuerySpace.appendSelectColumns(foreignEntityAliases, foreignMetaData);

        entityInitializerMap.put(nextUID, new EntityInitializer(nextUID, foreignEntityAliases, metaModel.getPersister(foreignMetaData.getTableClass())));
        visitedForeignColumnKey.add(foreignColumnType.getForeignColumnKey());
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);
    }

    @Override
    public void visit(ForeignCollectionColumnType foreignCollectionColumnType) {
        if (visitedForeignColumnKey.contains(foreignCollectionColumnType.getForeignColumnKey())) {
            LOGGER.debug("Detected circular references!");
            return;
        }
        DatabaseEntityMetadata<?> ownerMetaData = metaModel.getPersister(foreignCollectionColumnType.getOwnerClass()).getMetadata();
        EntityAliases ownerAliases = entityAliasResolverContext.getAliases(parentUidStack.peek());

        DatabaseEntityMetadata<?> foreignMetaData = metaModel.getPersister(foreignCollectionColumnType.getCollectionObjectClass()).getMetadata();
        String nextUID = uidGenerator.nextUID();

        EntityAliases foreignEntityAliases = entityAliasResolverContext.resolveAliases(nextUID, foreignMetaData);

        if (foreignCollectionColumnType.getFetchType().equals(FetchType.EAGER)) {
            entityQuerySpace.appendCollectionJoin(ownerMetaData.getPrimaryKey().getColumnName(), foreignCollectionColumnType, ownerAliases, foreignEntityAliases);
            entityQuerySpace.appendSelectColumns(foreignEntityAliases, foreignMetaData);
            entityInitializerMap.put(nextUID, new EntityInitializer(nextUID, foreignEntityAliases, metaModel.getPersister(foreignMetaData.getTableClass())));
        }
        CollectionLoader collectionLoader = new CollectionLoader(
                new CollectionQuerySpace(
                        new CollectionEntityAliases(
                                foreignEntityAliases.getKeyAlias(),
                                foreignEntityAliases.getAliasByColumnName(foreignCollectionColumnType.getForeignColumnKey().getColumnName()),
                                ownerAliases
                        ),
                        ownerMetaData.getPrimaryKey(),
                        foreignCollectionColumnType
                )
        );

        collectionInitializers.add(
                new CollectionInitializer(
                        parentUidStack.peek(),
                        collectionLoader
                )
        );
        visitedForeignColumnKey.add(foreignCollectionColumnType.getForeignColumnKey());
        parentUidStack.push(nextUID);
        foreignMetaData.accept(this);
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
    public boolean visit(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        return true;
    }

    public List<EntityInitializer> getEntityInitializers() {
        return new ArrayList<>(entityInitializerMap.values());
    }

    public List<CollectionInitializer> getCollectionInitializers() {
        return collectionInitializers;
    }

    public EntityQuerySpace getEntityQuerySpace() {
        return entityQuerySpace;
    }
}
