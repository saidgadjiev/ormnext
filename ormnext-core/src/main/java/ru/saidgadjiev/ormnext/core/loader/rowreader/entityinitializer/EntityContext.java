package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

/**
 * This class represent entity initializer.
 * It initialize entity object with two phases algorithm.
 * It mean we read all retrieved entity column values from result set object and save them to
 * temporary context {@link ResultSetContext} it is a first phase.
 * Then we set read values from first phase to entity object it is a second phase.
 *
 * @author Said Gadjiev
 */
public class EntityContext {

    /**
     * Uid generated from {@link UIDGenerator}.
     */
    private final String uid;

    /**
     * Current initializer entity persister.
     *
     * @see DatabaseEntityPersister
     */
    private final DatabaseEntityPersister persister;

    /**
     * Current initializer entity metadata.
     */
    private final DatabaseEntityMetadata<?> metadata;

    /**
     * Current entity column aliases.
     *
     * @see EntityAliases
     */
    private final EntityAliases entityAliases;

    /**
     * Create a new instance.
     *
     * @param uid           target uid
     * @param entityAliases target entity aliases
     * @param persister     target entity persister
     */
    public EntityContext(String uid, EntityAliases entityAliases, DatabaseEntityPersister persister) {
        this.uid = uid;
        this.persister = persister;
        this.entityAliases = entityAliases;

        metadata = persister.getMetadata();
    }

    /**
     * Return uid associated with this initializer.
     *
     * @return uid associated with this initializer
     */
    public String getUid() {
        return uid;
    }

    /**
     * Return aliases associated with this initializer.
     *
     * @return aliases associated with this initializer
     */
    public EntityAliases getEntityAliases() {
        return entityAliases;
    }

    public DatabaseEntityMetadata<?> getMetadata() {
        return metadata;
    }

    public DatabaseEntityPersister getPersister() {
        return persister;
    }
}
