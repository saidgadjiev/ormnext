package ru.saidgadjiev.ormnext.core.table.internal.metamodel;

import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.exception.NotRegisteredEntityFoundException;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Meta model.
 *
 * @author Said Gadjiev
 */
public class MetaModel {

    /**
     * Holds metadata by entity class.
     */
    private Map<Class<?>, DatabaseEntityPersister> metadataMap = new HashMap<>();

    /**
     * Persistent classes.
     */
    private Collection<Class<?>> persisterClasses;

    /**
     * Create a new meta model.
     *
     * @param persisterClasses persistent classes
     */
    public MetaModel(Collection<Class<?>> persisterClasses) {
        this.persisterClasses = persisterClasses;
    }

    /**
     * Initialize meta model.
     *
     * @param sessionManager session manager
     */
    public void init(SessionManager sessionManager) {
        for (Class<?> persisterClass : persisterClasses) {
            metadataMap.put(persisterClass, new DatabaseEntityPersister(
                            DatabaseEntityMetadata.build(persisterClass),
                            sessionManager
                    )
            );
        }
        for (DatabaseEntityPersister persister : metadataMap.values()) {
            persister.initialize(this);
        }
    }

    /**
     * Return entity persister by persister class.
     *
     * @param persisterClass target persister class
     * @return entity persister
     */
    public DatabaseEntityPersister getPersister(Class<?> persisterClass) {
        if (!metadataMap.containsKey(persisterClass)) {
            throw new NotRegisteredEntityFoundException(persisterClass);
        }

        return metadataMap.get(persisterClass);
    }

    /**
     * Return persister classes.
     *
     * @return persister classes
     */
    public Collection<Class<?>> getPersistentClasses() {
        return persisterClasses;
    }
}
