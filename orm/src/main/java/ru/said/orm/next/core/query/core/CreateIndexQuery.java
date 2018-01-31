package ru.said.orm.next.core.query.core;

import ru.said.orm.next.core.field.field_type.IndexFieldType;
import ru.said.orm.next.core.query.visitor.QueryElement;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

import java.util.List;

public class CreateIndexQuery implements QueryElement {

    private IndexFieldType indexFieldType;

    public CreateIndexQuery(IndexFieldType indexFieldType) {
        this.indexFieldType = indexFieldType;
    }


    public List<String> getColumns() {
        return indexFieldType.getColumns();
    }

    public String getIndexName() {
        return indexFieldType.getName();
    }

    public boolean isUnique() {
        return indexFieldType.isUnique();
    }

    public String getTableName() {
        return indexFieldType.getTableName();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
