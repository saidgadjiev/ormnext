package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.constraints.attribute.AttributeConstraint;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class AttributeDefinition implements QueryElement {

    private final String columnName;
    private final int dataType;
    private final int length;
    private List<AttributeConstraint> attributeConstraints = new ArrayList<>();

    public AttributeDefinition(String columnName, int dataType, int length) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.length = length;
    }

    public String getName() {
        return columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    public List<AttributeConstraint> getAttributeConstraints() {
        return attributeConstraints;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            attributeConstraints.forEach(attributeConstraint -> attributeConstraint.accept(visitor));
        }
    }
}
