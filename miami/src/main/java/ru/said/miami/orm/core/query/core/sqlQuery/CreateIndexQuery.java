package ru.said.miami.orm.core.query.core.sqlQuery;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.field.fieldTypes.IndexFieldType;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class CreateIndexQuery implements ISQLQuery {

    private List<String> columns;

    private String indexName;

    private String tableName;

    public QueryVisitor visitor;

    private boolean unique;

    private CreateIndexQuery(List<String> columns, String indexName, String tableName, boolean unique, QueryVisitor visitor) {
        this.columns = columns;
        this.indexName = indexName;
        this.visitor = visitor;
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
                indexFieldType.isUnique(),
                new DefaultVisitor()
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
