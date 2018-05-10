package ru.saidgadjiev.orm.next.core.field.fieldtype;

public class ForeignColumnKey {

    private final String tableName;

    private final String columnName;

    public ForeignColumnKey(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignColumnKey that = (ForeignColumnKey) o;

        if (!tableName.equals(that.tableName)) return false;
        return columnName.equals(that.columnName);
    }

    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        result = 31 * result + columnName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ForeignColumnKey{" +
                "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}
