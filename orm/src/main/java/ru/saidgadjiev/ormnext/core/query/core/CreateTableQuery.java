package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.constraints.table.TableConstraint;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class CreateTableQuery implements QueryElement {

    private List<AttributeDefinition> attributeDefinitions;

    private List<TableConstraint> tableConstraints = new ArrayList<>();

    private String typeName;

    private boolean ifNotExists;

    public CreateTableQuery(String typeName,
                             boolean ifNotExists,
                             List<AttributeDefinition> attributeDefinitions) {
        this.ifNotExists = ifNotExists;
        this.typeName = typeName;
        this.attributeDefinitions = attributeDefinitions;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<AttributeDefinition> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public List<TableConstraint> getTableConstraints() {
        return tableConstraints;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            attributeDefinitions.forEach(attributeDefinition -> attributeDefinition.accept(visitor));
            tableConstraints.forEach(tableConstraint -> tableConstraint.accept(visitor));
        }
    }
}
