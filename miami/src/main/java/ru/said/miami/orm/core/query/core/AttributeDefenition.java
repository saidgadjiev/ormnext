package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class AttributeDefenition implements QueryElement {
    private String name;

    private DataType dataType;

    private boolean id;

    private boolean generated;

    private int length;

    public AttributeDefenition(String name, DataType dataType, int length, boolean id, boolean generated) {
        this.name = name;
        this.dataType = dataType;
        this.length = length;
        this.id = id;
        this.generated = generated;
    }

    public String getName() {
        return name;
    }

    public boolean isId() {
        return id;
    }

    public boolean isGenerated() {
        return generated;
    }

    public DataType getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
