package ru.saidgadjiev.orm.next.core.table.internal.alias;

public class CollectionEntityAliases {

    private final String collectionColumnKeyAlias;

    private final String collectionOwnerAlias;

    private final Class<?> collectionObjectClass;

    public CollectionEntityAliases(String collectionColumnKeyAlias, String collectionOwnerAlias, Class<?> collectionObjectClass) {
        this.collectionColumnKeyAlias = collectionColumnKeyAlias;
        this.collectionOwnerAlias = collectionOwnerAlias;
        this.collectionObjectClass = collectionObjectClass;
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
}
