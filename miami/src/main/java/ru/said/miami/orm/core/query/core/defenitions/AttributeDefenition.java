package ru.said.miami.orm.core.query.core.defenitions;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.FieldType;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class AttributeDefenition implements QueryElement {

    private final String columnName;
    private final DataType dataType;
    private final int length;

    public AttributeDefenition(String columnName, DataType dataType, int length) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.length = length;
    }

    public String getName() {
        return columnName
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
