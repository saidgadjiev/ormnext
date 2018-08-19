package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.Cache;
import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection.source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.dao.context.CurrentSessionContext;
import ru.saidgadjiev.ormnext.core.dao.context.ThreadLocalCurrentSessionContext;
import ru.saidgadjiev.ormnext.core.loader.CacheEntityLoader;
import ru.saidgadjiev.ormnext.core.loader.DefaultEntityLoader;
import ru.saidgadjiev.ormnext.core.query.criteria.compiler.StatementCompiler;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

/**
 * Implementation of {@link SessionManager}.
 *
 * @author Said Gadjiev
 */
public class SessionManagerImpl implements SessionManager {

    /**
     * Connection source. Use for obtain new database connection.
     */
    private final ConnectionSource<?> dataSource;

    /**
     * Cache part.
     */
    private Cache cache;

    /**
     * Meta model.
     *
     * @see MetaModel
     */
    private MetaModel metaModel;

    /**
     * Database engine.
     *
     * @see DatabaseEngine
     */
    private DatabaseEngine<?> databaseEngine;

    /**
     * Statement compiler.
     *
     * @see StatementCompiler
     */
    private StatementCompiler statementCompiler;

    /**
     * Current session context.
     *
     * @see CurrentSessionContext
     */
    private CurrentSessionContext sessionContext;

    /**
     * Close flag.
     */
    private boolean closed = false;

    /**
     * Create new instance from requested options. It can be create only from {@link SessionManagerBuilder}.
     *
     * @param connectionSource  target connection source
     * @param metaModel         target meta model
     * @param databaseEngine    target database engine
     * @param statementCompiler target statement compiler
     * @throws SQLException any exceptions
     */
    SessionManagerImpl(ConnectionSource<?> connectionSource,
                       MetaModel metaModel,
                       DatabaseEngine<?> databaseEngine,
                       StatementCompiler statementCompiler) throws SQLException {
        this.dataSource = connectionSource;
        this.metaModel = metaModel;
        this.databaseEngine = databaseEngine;
        this.statementCompiler = statementCompiler;

        sessionContext = new ThreadLocalCurrentSessionContext(this);

        this.metaModel.init();
    }

    @Override
    public Session createSession() throws SQLException {
        DatabaseConnection<?> databaseConnection = dataSource.getConnection();

        if (cache != null) {
            return new SessionImpl(
                    dataSource,
                    new CacheEntityLoader(
                            cache,
                            metaModel,
                            new DefaultEntityLoader(
                                    databaseEngine,
                                    metaModel,
                                    statementCompiler
                            )
                    ),
                    databaseConnection,
                    this
            );
        } else {
            return new SessionImpl(
                    dataSource,
                    new DefaultEntityLoader(databaseEngine, metaModel, statementCompiler),
                    databaseConnection,
                    this
            );
        }
    }

    @Override
    public Session currentSession() throws SQLException {
        return sessionContext.currentSession();
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public void setObjectCache(Class<?> entityType, ObjectCache objectCache) {
        if (cache != null) {
            cache.setCache(entityType, objectCache);
        }
    }

    @Override
    public void setObjectCache(Class<?>[] entityClass, ObjectCache objectCache) {
        for (Class<?> entityType : entityClass) {
            setObjectCache(entityType, objectCache);
        }
    }

    @Override
    public void enableDefaultCache() {
        if (cache != null) {
            cache.enableDefaultCache();
        }
    }

    @Override
    public <T> DatabaseEngine<T> getDatabaseEngine() {
        return (DatabaseEngine<T>) databaseEngine;
    }

    @Override
    public void upgrade(Cache cache) {
        this.cache = cache;

        cache.init(metaModel, databaseEngine);
    }

    @Override
    public Cache getCache() {
        return cache;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws SQLException {
        if (cache != null) {
            cache.close();
        }
        dataSource.close();
        closed = true;
    }
}
