package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SessionManagerBuilder {

    private Collection<Class<?>> entityClasses = new ArrayList<>();

    private ConnectionSource connectionSource;

    private DatabaseType databaseType;

    private DatabaseEngine databaseEngine;

    public SessionManagerBuilder addEntityClasses(Class<?> ... classes) {
        entityClasses.addAll(Arrays.asList(classes));

        return this;
    }

    public SessionManagerBuilder connectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;

        return this;
    }

    public SessionManagerBuilder databaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;

        return this;
    }

    public SessionManagerBuilder databaseEngine(DatabaseEngine databaseEngine) {
        this.databaseEngine = databaseEngine;

        return this;
    }

    public SessionManager build() {
        DatabaseEngine engine = databaseEngine == null ? databaseType == null ? null : new DefaultDatabaseEngine(databaseType) : databaseEngine;

        if (engine == null) {
            throw new IllegalArgumentException("Please provide " + DatabaseType.class + " or " + DatabaseEngine.class);
        }
        MetaModel metaModel = new MetaModel(entityClasses);

        return new SessionManagerImpl(connectionSource, metaModel, engine);
    }
}
