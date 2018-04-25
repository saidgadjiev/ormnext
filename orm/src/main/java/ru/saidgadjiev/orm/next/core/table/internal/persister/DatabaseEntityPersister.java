package ru.saidgadjiev.orm.next.core.table.internal.persister;

import ru.saidgadjiev.orm.next.core.common.UIDGenerator;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowReader;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowReaderImpl;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer.EntityInitializer;
import ru.saidgadjiev.orm.next.core.table.internal.alias.EntityAliasResolverContext;
import ru.saidgadjiev.orm.next.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.table.internal.instatiator.EntityInstantiator;
import ru.saidgadjiev.orm.next.core.table.internal.instatiator.Instantiator;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.table.internal.queryspace.EntityQuerySpace;
import ru.saidgadjiev.orm.next.core.table.internal.visitor.DefaultEntityMetadataVisitor;

public class DatabaseEntityPersister {

    private EntityAliasResolverContext aliasResolverContext = new EntityAliasResolverContext();

    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    private Instantiator instantiator;

    private RowReader rowReader;

    private EntityInitializer rootEntityInitializer;

    private UIDGenerator uidGenerator = new UIDGenerator();

    private EntityQuerySpace entityQuerySpace;

    public DatabaseEntityPersister(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.instantiator = new EntityInstantiator(databaseEntityMetadata.getTableClass());

        initRootInitializer();
    }

    private void initRootInitializer() {
        String nextUID = uidGenerator.nextUID();
        EntityAliases entityAliases = aliasResolverContext.resolveAliases(nextUID, databaseEntityMetadata);

        rootEntityInitializer = new EntityInitializer(nextUID, entityAliases,this);
    }

    public void initialize(MetaModel metaModel) {
        DefaultEntityMetadataVisitor visitor = new DefaultEntityMetadataVisitor(
                databaseEntityMetadata,
                metaModel,
                aliasResolverContext,
                uidGenerator,
                rootEntityInitializer
        );

        databaseEntityMetadata.accept(visitor);
        rowReader = new RowReaderImpl(visitor.getEntityInitializers(), visitor.getCollectionInitializers(), rootEntityInitializer);
        entityQuerySpace = visitor.getEntityQuerySpace();
    }

    public DatabaseEntityMetadata<?> getMetadata() {
        return databaseEntityMetadata;
    }

    public Object instance() {
        return instantiator.instantiate();
    }

    public EntityQuerySpace getEntityQuerySpace() {
        return entityQuerySpace;
    }

    public RowReader getRowReader() {
        return rowReader;
    }
}
