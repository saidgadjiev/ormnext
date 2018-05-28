package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent INSERT query.
 */
public class CreateQuery implements QueryElement {

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Insert values.
     * @see InsertValues
     */
    private final List<InsertValues> updateValues = new ArrayList<>();

    /**
     * Insert column names.
     */
    private final List<String> columnNames = new ArrayList<>();

    /**
     * Create a new instance.
     * @param tableName target table name
     */
    public CreateQuery(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Add new insert values.
     * @param insertValues target insert values
     */
    public void add(InsertValues insertValues) {
        this.updateValues.add(insertValues);
    }

    /**
     * Return current insert values.
     * @return current insert values
     */
    public List<InsertValues> getInsertValues() {
        return updateValues;
    }

    /**
     * Add new insert column name.
     * @param columnName target column name
     */
    public void addColumnName(String columnName) {
        columnNames.add(columnName);
    }

    /**
     * Return current insert column names.
     * @return current insert column names
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Return current insert table name.
     * @return current insert table name
     */
    public String getTableName() {
        return tableName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            updateValues.forEach(updateValue -> updateValue.accept(visitor));
        }
    }
}
