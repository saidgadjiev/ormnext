package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.connection_source.ConnectionSource;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Builder for session manager.
 */
public class SessionManagerBuilder {

    /**
     * Entity classes.
     */
    private Collection<Class<?>> entityClasses = new ArrayList<>();

    /**
     * Connection source.
     */
    private ConnectionSource connectionSource;

    /**
     * Database type.
     * @see DatabaseType
     */
    private DatabaseType databaseType;

    /**
     * Database engine.
     * @see DatabaseEngine
     */
    private DatabaseEngine<?> databaseEngine;

    /**
     * Provide entity classes.
     * @param classes target classes
     * @return this for chain
     */
    public SessionManagerBuilder entities(Class<?> ... classes) {
        entityClasses.addAll(Arrays.asList(classes));

        return this;
    }

    /**
     * Provide connection source.
     * @param connectionSource target connection source
     * @return this for chain
     */
    public SessionManagerBuilder connectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;

        return this;
    }

    /**
     * Provide database type.
     * @param databaseType target database type
     * @return this for chain
     */
    public SessionManagerBuilder databaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;

        return this;
    }

    /**
     * Database engine.
     * @param databaseEngine target database engine
     * @return this for chain
     */
    public SessionManagerBuilder databaseEngine(DatabaseEngine<?> databaseEngine) {
        this.databaseEngine = databaseEngine;

        return this;
    }

    /**
     * Create session manager. If database engine not provided use {@link DefaultDatabaseEngine}.
     * @return this for chain
     */
    public SessionManager build() {
        DatabaseEngine engine = databaseEngine == null
                ? databaseType == null
                ? null : new DefaultDatabaseEngine(databaseType) : databaseEngine;

        if (engine == null) {
            throw new IllegalArgumentException("Please provide " + DatabaseType.class + " or " + DatabaseEngine.class);
        }
        MetaModel metaModel = new MetaModel(entityClasses);

        return new SessionManagerImpl(connectionSource, metaModel, engine);
    }
}
