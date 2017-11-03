package ru.said.miami.orm.core.query.core.defenitions;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.constraints.attribute.AttributeConstraint;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

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
        visitor.start(this);
        visitor.finish(this);
    }
}
