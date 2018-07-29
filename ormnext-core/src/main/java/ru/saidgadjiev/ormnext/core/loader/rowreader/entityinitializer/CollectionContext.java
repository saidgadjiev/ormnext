package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.object.collection.CollectionLoader;
import ru.saidgadjiev.ormnext.core.table.internal.alias.CollectionEntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.alias.UIDGenerator;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

/**
 * This class use for initialize entity column type
 * annotated with {@link ru.saidgadjiev.ormnext.core.field.ForeignCollectionField}.
 * It use two phases algorithm for initialize collection.
 * First phase:
 * Read all collection object ids.
 * Second phase:
 * Retrieve collection objects by them ids and add them to column type collection.
 *
 * @author Said Gadjiev
 */
public class CollectionContext {

    /**
     * Collection owner uid generated with {@link UIDGenerator}.
     */
    private String uid;

    /**
     * Collection load helper class.
     *
     * @see CollectionLoader
     */
    private final CollectionLoader collectionLoader;

    /**
     * Database metadata.
     */
    private DatabaseEntityMetadata<?> metadata;

    /**
     * Aliases.
     */
    private final CollectionEntityAliases aliases;

    /**
     * Create a new instance.
     *
     * @param metadata         collection owner database entity metadata
     * @param aliases          collection aliases
     * @param ownerUID         owner uid
     * @param collectionLoader collection load helper
     */
    public CollectionContext(String ownerUID,
                             CollectionEntityAliases aliases,
                             CollectionLoader collectionLoader,
                             DatabaseEntityMetadata<?> metadata) {
        this.uid = ownerUID;
        this.collectionLoader = collectionLoader;

        this.metadata = metadata;
        this.aliases = aliases;
    }

    /**
     * Return collection aliases.
     *
     * @return collection aliases
     */
    public CollectionEntityAliases getAliases() {
        return aliases;
    }

    /**
     * Return uid.
     *
     * @return uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Return database metadata.
     *
     * @return database metadata
     */
    public DatabaseEntityMetadata<?> getMetadata() {
        return metadata;
    }

    /**
     * Return collection loader.
     *
     * @return collection loader
     */
    public CollectionLoader getCollectionLoader() {
        return collectionLoader;
    }

    /**
     * Return collection column type.
     *
     * @return collection column type
     */
    public ForeignCollectionColumnTypeImpl getColumnType() {
        return collectionLoader.getForeignCollectionColumnType();
    }
}
