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
    private final String collectionObjectKeyAlias;

    /**
     * Foreign column alias.
     */
    private String foreignColumnAlias;

    /**
     * Create a new instance.
     *
     * @param collectionObjectKeyAlias target collection owner key column alias
     * @param foreignColumnAlias       target foreign column alias
     */
    public CollectionEntityAliases(String collectionObjectKeyAlias, String foreignColumnAlias) {
        this.collectionObjectKeyAlias = collectionObjectKeyAlias;
        this.foreignColumnAlias = foreignColumnAlias;
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
     * Foreign column alias.
     *
     * @return foreign column alias
     */
    public String getForeignColumnAlias() {
        return foreignColumnAlias;
    }
}
