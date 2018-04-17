package ru.saidgadjiev.orm.next.core.dao.visitor;

public interface EntityElement {

    void accept(EntityMetadataVisitor visitor);
}
