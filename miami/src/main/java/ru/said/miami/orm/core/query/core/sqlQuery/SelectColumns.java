package ru.said.miami.orm.core.query.core.sqlQuery;

import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by said on 04.11.17.
 */
public class SelectColumns implements SelectExpression {

    List<String> columns = new ArrayList<>();

    public void addColumn(String columnName) {
        columns.add(columnName);
    }

    public void addAll(Collection<String> columns) {
        this.columns.addAll(columns);
    }

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
