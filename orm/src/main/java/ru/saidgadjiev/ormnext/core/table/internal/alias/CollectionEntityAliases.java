package ru.saidgadjiev.ormnext.core.table.internal.alias;

import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

public class CollectionEntityAliases {

    private final String collectionColumnKeyAlias;

    private final String collectionOwnerAlias;

    private final EntityAliases ownerAliases;

    public CollectionEntityAliases(String collectionColumnKeyAlias,
                                   String collectionOwnerAlias,
                                   EntityAliases ownerAliases) {
        this.collectionColumnKeyAlias = collectionColumnKeyAlias;
        this.collectionOwnerAlias = collectionOwnerAlias;
        this.ownerAliases = ownerAliases;
    }

    public String getCollectionColumnKeyAlias() {
        return collectionColumnKeyAlias;
    }

    public String getCollectionOwnerAlias() {
        return collectionOwnerAlias;
    }

    public EntityAliases getOwnerAliases() {
        return ownerAliases;
    }
}
