package ru.saidgadjiev.orm.next.core.dao.visitor;

public class EntityReference {

    private final String uid;

    public EntityReference(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
