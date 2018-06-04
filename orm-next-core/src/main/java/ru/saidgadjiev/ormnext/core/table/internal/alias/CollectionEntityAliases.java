package ru.saidgadjiev.ormnext.core.table.internal.alias;

/**
 * Collection entity aliases.
 *
 * @author said gadjiev
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

    /**
     * Return collection object key alias.
     *
     * @return collection object key alias
     */
    public String getCollectionObjectKeyAlias() {
        return collectionObjectKeyAlias;
    }
}
