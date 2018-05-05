package ru.saidgadjiev.ormnext.core.query.core.constraints.table;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class ForeignKeyConstraint implements TableConstraint {

    private String columnName;

    private String typeName;

    private String foreignColumnName;

    public ForeignKeyConstraint(String typeName, String foreignColumnName, String columnName) {
        this.typeName = typeName;
        this.foreignColumnName = foreignColumnName;
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
