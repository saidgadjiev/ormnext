package ru.saidgadjiev.ormnext.core.table.internal.alias;

/**
 * Collection entity aliases.
 */
public class CollectionEntityAliases {

    /**
     * Collection column key alias.
     */
    private final String collectionObjectKeyAlias;

    /**
     * Create a new instance.
     * @param collectionObjectKeyAlias collection owner key column alias
     */
    public CollectionEntityAliases(String collectionObjectKeyAlias) {
        this.collectionObjectKeyAlias = collectionObjectKeyAlias;
    }

    public String getCollectionObjectKeyAlias() {
        return collectionObjectKeyAlias;
    }
}
