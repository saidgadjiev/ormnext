package ru.saidgadjiev.ormnext.core.table.internal.visitor;

public interface EntityElement {

    void accept(EntityMetadataVisitor visitor);
}
