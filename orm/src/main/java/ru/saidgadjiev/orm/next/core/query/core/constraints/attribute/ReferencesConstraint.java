package ru.saidgadjiev.orm.next.core.query.core.constraints.attribute;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

public class ReferencesConstraint implements AttributeConstraint {

    private String typeName;

    private String columnName;

    public ReferencesConstraint(String typeName, String columnName) {
        this.typeName = typeName;
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
