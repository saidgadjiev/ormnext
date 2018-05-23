package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.field.fieldtype.IndexFieldType;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.List;

/**
 * This class represent create index query.
 */
public class CreateIndexQuery implements QueryElement {

    /**
     * Indexed columns info.
     * @see IndexFieldType
     */
    private IndexFieldType indexFieldType;

    /**
     * Create new instance.
     * @param indexFieldType target indexed columns info
     */
    public CreateIndexQuery(IndexFieldType indexFieldType) {
        this.indexFieldType = indexFieldType;
    }

    /**
     * Return indexed column names.
     * @return indexed column names
     */
    public List<String> getColumns() {
        return indexFieldType.getColumns();
    }

    /**
     * Return index name.
     * @return index name
     */
    public String getIndexName() {
        return indexFieldType.getName();
    }

    /**
     * Return is unique index
     * @return true if it is unique index else false
     */
    public boolean isUnique() {
        return indexFieldType.isUnique();
    }

    /**
     * Return indexed table table name.
     * @return indexed table table name
     */
    public String getTableName() {
        return indexFieldType.getTableName();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
