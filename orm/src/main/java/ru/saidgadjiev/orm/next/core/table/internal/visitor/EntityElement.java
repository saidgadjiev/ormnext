package ru.saidgadjiev.orm.next.core.table.internal.visitor;

public interface EntityElement {

    void accept(EntityMetadataVisitor visitor);
}
