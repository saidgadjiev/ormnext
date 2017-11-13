package ru.said.miami.orm.core.query.core.queryBuilder;

import ru.said.miami.orm.core.query.core.sqlQuery.Select;
import ru.said.miami.orm.core.table.TableInfo;

import java.sql.SQLException;

public class QueryBuilder<T> {

    private Where where;

    private Select selectQuery;

    public QueryBuilder(TableInfo<T> tableInfo) {
        this.selectQuery = Select.buildQueryForAll(tableInfo.getTableName());
        this.where = new Where<>(this, selectQuery.getWhere());
    }

    public Where where() {
        return where;
    }

    public PreparedQuery prepare() throws SQLException {
        return new PreparedQuery(selectQuery.query());
    }
}
