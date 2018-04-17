package ru.saidgadjiev.orm.next.core.dao.metamodel;

import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.persister.DatabaseEntityPersister;
import ru.saidgadjiev.orm.next.core.table.persister.DatabaseEntityPersisterImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaModel {

    private Map<Class<?>, DatabaseEntityPersister> metadataMap = new HashMap<>();

    public DatabaseEntityPersister getMetaData(Class<?> metaDataClass) {
        return metadataMap.get(metaDataClass);
    }

    public void initialize(Collection<Class<?>> persisterClasses) {
        for (Class<?> persisterClass: persisterClasses) {
            metadataMap.put(persisterClass, new DatabaseEntityPersisterImpl(DatabaseEntityMetadata.build(persisterClass)));
        }
    }
}
