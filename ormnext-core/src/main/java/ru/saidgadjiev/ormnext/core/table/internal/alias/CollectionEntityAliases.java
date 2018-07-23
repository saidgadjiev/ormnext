package ru.saidgadjiev.ormnext.core.table.internal.alias;

/**
 * Collection entity aliases.
 *
 * @author Said Gadjiev
 */
public class CollectionEntityAliases {

    /**
     * Collection column key alias.
     */
    private String collectionObjectKeyAlias;

    /**
     * Collection column key alias.
     */
    private String collectionOwnerColumnKeyAlias;

    /**
     * Create a new instance.
     *
     * @param collectionObjectKeyAlias      target collection owner key column alias
     * @param collectionOwnerColumnKeyAlias target collection owner key column alias
     */
    public CollectionEntityAliases(String collectionObjectKeyAlias, String collectionOwnerColumnKeyAlias) {
        this.collectionObjectKeyAlias = collectionObjectKeyAlias;
        this.collectionOwnerColumnKeyAlias = collectionOwnerColumnKeyAlias;
    }

    /**
     * Return collection object key alias.
     *
     * @return collection object key alias
     */
    public String getCollectionObjectKeyAlias() {
        return collectionObjectKeyAlias;
    }

    /**
     * Return collection column key alias.
     *
     * @return collection column key alias
     */
    public String getCollectionOwnerColumnKeyAlias() {
        return collectionOwnerColumnKeyAlias;
    }
}
