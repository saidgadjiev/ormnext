package ru.said.orm.next.core.query.core;

import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.IndexFieldType;
import ru.said.orm.next.core.query.visitor.QueryElement;
import ru.said.orm.next.core.query.visitor.QueryVisitor;
import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.IndexFieldType;
import ru.said.orm.next.core.query.visitor.QueryElement;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class CreateIndexQuery implements QueryElement {

    private List<String> columns;

    private String indexName;

    private String tableName;

    private boolean unique;

    private CreateIndexQuery(List<String> columns, String indexName, String tableName, boolean unique) {
        this.columns = columns;
        this.indexName = indexName;
        this.unique = unique;
        this.tableName = tableName;
    }

    public static CreateIndexQuery build(IndexFieldType indexFieldType) {
        return new CreateIndexQuery(
                indexFieldType.getDbFieldTypes()
                        .stream()
                        .map(DBFieldType::getColumnName)
                        .collect(Collectors.toList()),
                indexFieldType.getName(),
                indexFieldType.getTableName(),
                indexFieldType.isUnique()
        );
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean isUnique() {
        return unique;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
