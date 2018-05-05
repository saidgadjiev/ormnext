package ru.saidgadjiev.ormnext.core.table.internal.queryspace;

public class EntityReference {

    private final String uid;

    public EntityReference(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
