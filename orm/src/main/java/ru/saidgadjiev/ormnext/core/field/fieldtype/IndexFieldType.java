package ru.saidgadjiev.ormnext.core.field.fieldtype;

import java.util.List;

public class IndexFieldType {

    private List<String> columns;

    private String name;

    private boolean unique;

    private String tableName;

    public IndexFieldType(String name, boolean unique, String tableName, List<String> columns) {
        this.columns = columns;
        this.name = name;
        this.tableName = tableName;
        this.unique = unique;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isUnique() {
        return unique;
    }

}
