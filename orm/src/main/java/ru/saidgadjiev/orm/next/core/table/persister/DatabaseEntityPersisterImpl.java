package ru.saidgadjiev.orm.next.core.table.persister;

import ru.saidgadjiev.orm.next.core.common.UIDGenerator;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.SessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.dao.visitor.DefaultEntityMetadataVisitor;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromExpression;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliasResolverContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.EntityInitializer;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowReader;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowReaderImpl;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.persister.instatiator.EntityInstantiator;
import ru.saidgadjiev.orm.next.core.table.persister.instatiator.Instantiator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseEntityPersisterImpl implements DatabaseEntityPersister {

    private EntityAliasResolverContext aliasResolverContext = new EntityAliasResolverContext();

    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    private SessionManagerImpl sessionManager;

    private Instantiator instantiator;

    private RowReader rowReader;

    private EntityInitializer rootEntityInitializer;

    private UIDGenerator uidGenerator = new UIDGenerator();

    private FromExpression fromExpression;

    private SelectColumnsList selectColumnsList;

    public DatabaseEntityPersisterImpl(DatabaseEntityMetadata<?> databaseEntityMetadata, SessionManagerImpl sessionManager) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.instantiator = new EntityInstantiator(databaseEntityMetadata.getTableClass());
        this.sessionManager = sessionManager;

        initRootInitializer();
    }

    private void initRootInitializer() {
        String nextUID = uidGenerator.nextUID();
        EntityAliases entityAliases = aliasResolverContext.resolveAliases(nextUID, databaseEntityMetadata);

        rootEntityInitializer = new EntityInitializer(nextUID, entityAliases,this);
    }

    @Override
    public List<Object> load(Session session, DatabaseResults databaseResults) throws SQLException {
        ResultSetContext resultSetContext = new ResultSetContext(session, databaseResults);
        List<Object> results = new ArrayList<>();

        while(databaseResults.next()) {
            results.add(rowReader.startRead(resultSetContext));
            rowReader.finishRead(resultSetContext);
        }

        return results;
    }

    @Override
    public void initialize() {
        DefaultEntityMetadataVisitor visitor = new DefaultEntityMetadataVisitor(
                databaseEntityMetadata,
                sessionManager.getMetaModel(),
                aliasResolverContext,
                uidGenerator,
                rootEntityInitializer
        );

        databaseEntityMetadata.accept(visitor);
        aliasResolverContext = visitor.getEntityAliasResolverContext();
        rowReader = new RowReaderImpl(visitor.getEntityInitializers(), null, rootEntityInitializer);
        fromExpression = visitor.getFromJoinedTables();
        selectColumnsList = visitor.getSelectColumnsList();
    }

    @Override
    public DatabaseEntityMetadata<?> getMetadata() {
        return databaseEntityMetadata;
    }

    @Override
    public Object instance() {
        return instantiator.instantiate();
    }

    @Override
    public EntityAliases getAliases() {
        return rootEntityInitializer.getEntityAliases();
    }

    @Override
    public FromExpression getFromExpression() {
        return fromExpression;
    }

    @Override
    public SelectColumnsList getSelectColumnsList() {
        return selectColumnsList;
    }

    @Override
    public MetaModel getMetaModel() {
        return sessionManager.getMetaModel();
    }

}
