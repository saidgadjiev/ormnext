package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class AttributeDefenition implements QueryElement {

    private DBFieldType fieldType;

    public AttributeDefenition(DBFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getName() {
        return fieldType.getFieldName();
    }

    public boolean isId() {
        return fieldType.isId();
    }

    public boolean isGenerated() {
        return fieldType.isGenerated();
    }

    public DataType getDataType() {
        return fieldType.getDataType();
    }

    public int getLength() {
        return fieldType.getLength();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
