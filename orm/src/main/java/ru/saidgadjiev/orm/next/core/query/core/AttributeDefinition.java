package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.query.core.constraints.attribute.AttributeConstraint;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class AttributeDefinition implements QueryElement {

    private final String columnName;
    private final DataType dataType;
    private final int length;
    private List<AttributeConstraint> attributeConstraints = new ArrayList<>();

    public AttributeDefinition(String columnName, DataType dataType, int length) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.length = length;
    }

    public String getName() {
        return columnName;
    }

    public DataType getDataType() {
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
        visitor.visit(this);

    }
}
