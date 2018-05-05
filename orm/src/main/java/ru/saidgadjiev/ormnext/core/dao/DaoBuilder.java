package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.support.ConnectionSource;
import ru.saidgadjiev.ormnext.core.db.DatabaseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DaoBuilder {

    private Collection<Class<?>> entityClasses = new ArrayList<>();

    private ConnectionSource connectionSource;

    private DatabaseType databaseType;

    private DatabaseEngine databaseEngine;

    public DaoBuilder addEntityClasses(Class<?> ... classes) {
        entityClasses.addAll(Arrays.asList(classes));

        return this;
    }

    public DaoBuilder connectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;

        return this;
    }

    public DaoBuilder databaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;

        return this;
    }

    public DaoBuilder databaseEngine(DatabaseEngine databaseEngine) {
        this.databaseEngine = databaseEngine;

        return this;
    }

    public Dao build() {
        DatabaseEngine engine = databaseEngine == null ? databaseType == null ? null : new DefaultDatabaseEngine(databaseType) : databaseEngine;

        if (engine == null) {
            throw new IllegalArgumentException("Please provide " + DatabaseType.class + " or " + DatabaseEngine.class);
        }

        return new DaoImpl(connectionSource, entityClasses, engine);
    }
}
