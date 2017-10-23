package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DropTableQuery implements Query<Boolean>, QueryElement {

    private String tableName;
    private QueryVisitor visitor;

    private DropTableQuery(String tableName, QueryVisitor visitor) {
        this.tableName = tableName;
        this.visitor = visitor;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }

    public static DropTableQuery buildQuery(String typeName) {
        return new DropTableQuery(
                typeName,
                new DefaultVisitor()
        );
    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        this.accept(visitor);
        try (Statement statement = connection.createStatement()) {
            statement.execute(visitor.getQuery());

            return true;
        }
    }
}
