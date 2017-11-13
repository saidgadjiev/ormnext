package ru.said.miami.orm.core.query.core.sqlQuery;

import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

public class DropTableQuery implements QueryElement {

    private String tableName;
    private boolean ifExists;
    private QueryVisitor visitor;

    private DropTableQuery(String tableName, boolean ifExists, QueryVisitor visitor) {
        this.tableName = tableName;
        this.ifExists = ifExists;
        this.visitor = visitor;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }

    public static DropTableQuery buildQuery(String typeName, boolean ifExists) {
        return new DropTableQuery(
                typeName,
                ifExists,
                new DefaultVisitor()
        );
    }
}
