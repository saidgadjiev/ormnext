package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.IndexFieldType;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CreateIndexQuery implements Query<Boolean>, QueryElement {

    private List<String> columns;

    private String indexName;

    public QueryVisitor visitor;

    private CreateIndexQuery(List<String> columns, String indexName, QueryVisitor visitor) {
        this.columns = columns;
        this.indexName = indexName;
        this.visitor = visitor;
    }

    public static<T> CreateIndexQuery build(IndexFieldType indexFieldType) {
        return new CreateIndexQuery(indexFieldType.getDbFieldTypes()
                .stream()
                .map(DBFieldType::getColumnName)
                .collect(Collectors.toList()), indexFieldType.getName(), new DefaultVisitor());
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getIndexName() {
        return indexName;
    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        this.accept(visitor);
        String sql = visitor.getQuery();

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);

            return true;
        }
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
