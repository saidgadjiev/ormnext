package ru.saidgadjiev.orm.next.core.table.internal.metamodel;

import ru.saidgadjiev.orm.next.core.table.internal.persister.DatabaseEntityPersister;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaModel {

    private Map<Class<?>, DatabaseEntityPersister> metadataMap = new HashMap<>();

    public MetaModel(Collection<Class<?>> persisterClasses) {
        for (Class<?> persisterClass: persisterClasses) {
            metadataMap.put(persisterClass, new DatabaseEntityPersister(DatabaseEntityMetadata.build(persisterClass)));
        }
        for (DatabaseEntityPersister persister: metadataMap.values()) {
            persister.initialize(this);
        }
    }

    public DatabaseEntityPersister getPersister(Class<?> metaDataClass) {
        return metadataMap.get(metaDataClass);
    }

    public Collection<Class<?>> getPersistentClasses() {
        return metadataMap.keySet();
    }
}
