package ru.saidgadjiev.ormnext.core.table.internal.persister;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.loader.object.OrmNextMethodHandler;
import ru.saidgadjiev.ormnext.core.loader.rowreader.RowReader;
import ru.saidgadjiev.ormnext.core.loader.rowreader.RowReaderImpl;
import ru.saidgadjiev.ormnext.core.loader.rowreader.RowResult;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.EntityContext;
import ru.saidgadjiev.ormnext.core.query.space.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliasResolverContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.instatiator.Instantiator;
import ru.saidgadjiev.ormnext.core.table.internal.instatiator.ObjectInstantiator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.DefaultEntityMetadataVisitor;
import ru.saidgadjiev.proxymaker.ProxyMaker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity persister. Holds entity aliases and aliases for entity references.
 *
 * @author Said Gadjiev
 */
public class DatabaseEntityPersister {

    /**
     * Alias context.
     *
     * @see EntityAliasResolverContext
     */
    private EntityAliasResolverContext aliasResolverContext = new EntityAliasResolverContext();

    /**
     * Entity meta data.
     */
    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    /**
     * Entity object instantiator.
     */
    private Instantiator instantiator;

    /**
     * Entity results reader.
     *
     * @see RowReader
     */
    private RowReader rowReader;

    /**
     * Entity root initializer.
     *
     * @see EntityContext
     */
    private EntityContext rootEntityContext;

    /**
     * Unique uid generator. It use for associate aliases with entity initializer.
     */
    private UIDGenerator uidGenerator = new UIDGenerator();

    /**
     * Proxy factory. Use for lazy fetch {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}.
     *
     * @see ProxyMaker
     */
    private ProxyMaker proxyMaker = new ProxyMaker();

    /**
     * Entity query space.
     *
     * @see EntityQuerySpace
     */
    private EntityQuerySpace entityQuerySpace;

    /**
     * Create a new instance.
     *
     * @param databaseEntityMetadata entity metadata
     */
    public DatabaseEntityPersister(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.instantiator = new ObjectInstantiator(databaseEntityMetadata.getTableClass());

        initRootInitializer();
    }

    /**
     * Initialize entity initializer {@link EntityContext} for root entity.
     */
    private void initRootInitializer() {
        String nextUID = uidGenerator.nextUID();
        EntityAliases entityAliases = aliasResolverContext.resolveAliases(nextUID, databaseEntityMetadata);

        rootEntityContext = new EntityContext(nextUID, entityAliases, this);
    }

    /**
     * Load entity object list from database results.
     *
     * @param session         target session
     * @param databaseResults target database results
     * @return object list
     * @throws SQLException any SQL exceptions
     */
    public List<RowResult> load(Session session, DatabaseResults databaseResults) throws SQLException {
        ResultSetContext resultSetContext = new ResultSetContext(
                session,
                databaseResults
        );
        List<RowResult> results = new ArrayList<>();

        while (databaseResults.next()) {
            RowResult rowResult = rowReader.startRead(resultSetContext);

            if (rowResult.isNew()) {
                results.add(rowResult);
            }
        }
        rowReader.finishRead(resultSetContext);

        return results;
    }

    /**
     * Perssiter initialize method.
     *
     * @param metaModel target meta model
     * @see MetaModel
     */
    public void initialize(MetaModel metaModel) throws SQLException {
        DefaultEntityMetadataVisitor visitor = new DefaultEntityMetadataVisitor(
                databaseEntityMetadata,
                metaModel,
                aliasResolverContext,
                uidGenerator,
                rootEntityContext
        );

        databaseEntityMetadata.accept(visitor);
        rowReader = new RowReaderImpl(
                visitor.getEntityInitializers(),
                visitor.getCollectionContexts(),
                rootEntityContext
        );
        entityQuerySpace = visitor.getEntityQuerySpace();
    }

    /**
     * Create new proxy object which will be use for lazy instantiation
     * {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}.
     *
     * @param entityClass     target entity class
     * @param key             target entity id
     * @param keyPropertyName target key property name
     * @return new proxy object
     * @throws SQLException any SQL exceptions
     */
    public Object createProxy(Session session, Class<?> entityClass, String keyPropertyName, Object key) throws SQLException {
        try {
            return proxyMaker
                    .superClass(entityClass)
                    .make(new OrmNextMethodHandler(session, entityClass, keyPropertyName, key));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Return current entity metadata.
     *
     * @return current metadata
     */
    public DatabaseEntityMetadata<?> getMetadata() {
        return databaseEntityMetadata;
    }

    /**
     * Create new entity class object.
     *
     * @return new object
     */
    public Object instance() {
        return instantiator.instantiate();
    }

    /**
     * Return current entity query space.
     *
     * @return entity query space
     */
    public EntityQuerySpace getEntityQuerySpace() {
        return entityQuerySpace;
    }

    /**
     * Return entity aliases.
     *
     * @return entity aliases
     */
    public EntityAliases getAliases() {
        return rootEntityContext.getEntityAliases();
    }
}
