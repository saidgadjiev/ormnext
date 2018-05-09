package ru.saidgadjiev.ormnext.core.table.internal.persister;

import ru.saidgadjiev.ormnext.core.common.UIDGenerator;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.stamentexecutor.object.OrmNextMethodHandler;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.RowReader;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.RowReaderImpl;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.EntityInitializer;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliasResolverContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.instatiator.Instantiator;
import ru.saidgadjiev.ormnext.core.table.internal.instatiator.ObjectInstantiator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.queryspace.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.DefaultEntityMetadataVisitor;
import ru.saidgadjiev.proxymaker.ProxyMaker;

public class DatabaseEntityPersister {

    private EntityAliasResolverContext aliasResolverContext = new EntityAliasResolverContext();

    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    private Instantiator instantiator;

    private SessionManager sessionManager;

    private RowReader rowReader;

    private EntityInitializer rootEntityInitializer;

    private UIDGenerator uidGenerator = new UIDGenerator();

    private ProxyMaker proxyMaker = new ProxyMaker();

    private EntityQuerySpace entityQuerySpace;

    public DatabaseEntityPersister(DatabaseEntityMetadata<?> databaseEntityMetadata, SessionManager sessionManager) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.instantiator = new ObjectInstantiator(databaseEntityMetadata.getTableClass());
        this.sessionManager = sessionManager;

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

    public Object createProxy(Class<?> entityClass, Object id) {
        try {
            return proxyMaker
                    .superClass(entityClass)
                    .make(new Class[0], new Object[0], new OrmNextMethodHandler(sessionManager, entityClass, id));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
