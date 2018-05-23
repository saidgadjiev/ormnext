package ru.saidgadjiev.ormnext.core.table.internal.metamodel;

import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.exception.NotRegisteredEntityFoundException;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaModel {

    private Map<Class<?>, DatabaseEntityPersister> metadataMap = new HashMap<>();

    private Collection<Class<?>> persisterClasses;

    public MetaModel(Collection<Class<?>> persisterClasses) {
        this.persisterClasses = persisterClasses;
    }

    public void init(SessionManager sessionManager) {
        for (Class<?> persisterClass: persisterClasses) {
            metadataMap.put(persisterClass, new DatabaseEntityPersister(DatabaseEntityMetadata.build(persisterClass), sessionManager));
        }
        for (DatabaseEntityPersister persister: metadataMap.values()) {
            persister.initialize(this);
        }
    }

    public DatabaseEntityPersister getPersister(Class<?> metaDataClass) {
        if (!metadataMap.containsKey(metaDataClass)) {
            throw new NotRegisteredEntityFoundException(metaDataClass);
        }
        
        return metadataMap.get(metaDataClass);
    }

    public Collection<Class<?>> getPersistentClasses() {
        return metadataMap.keySet();
    }
}
