package ru.saidgadjiev.orm.next.core.table.persister;

import ru.saidgadjiev.orm.next.core.dao.visitor.DefaultEntityMetadataVisitor;
import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliasResolverContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.EntityInitializer;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DatabaseEntityPersisterImpl implements DatabaseEntityPersister {

    private EntityAliasResolverContext aliasResolverContext;

    private Collection<EntityInitializer> entityInitializers = new ArrayList<>();

    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    public DatabaseEntityPersisterImpl(DatabaseEntityMetadata<?> databaseEntityMetadata) {
        this.databaseEntityMetadata = databaseEntityMetadata;
    }

    public Object readRow(DatabaseResults databaseResults) {
        for (EntityInitializer entityInitializer: entityInitializers) {
            entityInitializer.startRead(databaseResults);
        }

        return null;
    }

    @Override
    public void initialize() {
        DefaultEntityMetadataVisitor visitor = new DefaultEntityMetadataVisitor(databaseEntityMetadata, null);

        databaseEntityMetadata.accept(visitor);
        aliasResolverContext = visitor.getEntityAliasResolverContext();
        Map<Class<?>, String> uidMap = visitor.getMetaDataUidMap();

        for (Map.Entry<Class<?>, String> entry: uidMap.entrySet()) {
            entityInitializers.add(new EntityInitializer(aliasResolverContext.getAliases(entry.getValue()), entry.getKey()));
        }
    }

    @Override
    public DatabaseEntityMetadata<?> getMetadata() {
        return databaseEntityMetadata;
    }
}
