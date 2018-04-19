package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

public class CollectionEntityAliases {

    private final String collectionColumnAlias;

    public CollectionEntityAliases(String collectionColumnAlias) {
        this.collectionColumnAlias = collectionColumnAlias;
    }

    public String getCollectionColumnAlias() {
        return collectionColumnAlias;
    }
}
