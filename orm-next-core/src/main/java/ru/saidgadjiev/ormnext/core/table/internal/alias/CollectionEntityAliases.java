package ru.saidgadjiev.ormnext.core.table.internal.alias;

/**
 * Collection entity aliases.
 */
public class CollectionEntityAliases {

    /**
     * Collection column key alias.
     */
    private final String collectionOwnerColumnKeyAlias;

    /**
     * Create a new instance.
     * @param collectionOwnerColumnKeyAlias collection owner key column alias
     */
    public CollectionEntityAliases(String collectionOwnerColumnKeyAlias) {
        this.collectionOwnerColumnKeyAlias = collectionOwnerColumnKeyAlias;
    }

    /**
     * Return collection column key alias.
     * @return collection column key alias
     */
    public String getCollectionOwnerColumnKeyAlias() {
        return collectionOwnerColumnKeyAlias;
    }
}
