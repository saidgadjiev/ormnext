package ru.saidgadjiev.orm.next.core.table.internal.alias;

public class CollectionEntityAliases {

    private final String collectionColumnKeyAlias;

    private final String collectionOwnerAlias;

    private final Class<?> collectionObjectClass;

    private final EntityAliases ownerAliases;

    public CollectionEntityAliases(String collectionColumnKeyAlias, String collectionOwnerAlias, Class<?> collectionObjectClass, EntityAliases ownerAliases) {
        this.collectionColumnKeyAlias = collectionColumnKeyAlias;
        this.collectionOwnerAlias = collectionOwnerAlias;
        this.collectionObjectClass = collectionObjectClass;
        this.ownerAliases = ownerAliases;
    }

    public String getCollectionColumnKeyAlias() {
        return collectionColumnKeyAlias;
    }

    public Class<?> getCollectionObjectClass() {
        return collectionObjectClass;
    }

    public String getCollectionOwnerAlias() {
        return collectionOwnerAlias;
    }

    public EntityAliases getOwnerAliases() {
        return ownerAliases;
    }
}
