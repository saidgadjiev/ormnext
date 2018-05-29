package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.field.field_type.IndexColumns;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.List;

/**
 * This class represent create index query.
 */
public class CreateIndexQuery implements QueryElement {

    /**
     * Indexed columns info.
     * @see IndexColumns
     */
    private IndexColumns indexColumns;

    /**
     * Create a new instance.
     * @param indexColumns target indexed columns info
     */
    public CreateIndexQuery(IndexColumns indexColumns) {
        this.indexColumns = indexColumns;
    }

    /**
     * Return indexed column names.
     * @return indexed column names
     */
    public List<String> getColumns() {
        return indexColumns.getColumns();
    }

    /**
     * Return index name.
     * @return index name
     */
    public String getIndexName() {
        return indexColumns.getName();
    }

    /**
     * Return is unique index.
     * @return true if it is unique index else false
     */
    public boolean isUnique() {
        return indexColumns.isUnique();
    }

    /**
     * Return indexed table table name.
     * @return indexed table table name
     */
    public String getTableName() {
        return indexColumns.getTableName();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
