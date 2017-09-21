package ru.said.miami.orm.core.cache.core.clause;

import ru.said.miami.orm.core.cache.core.dao.Dao;
import ru.said.miami.orm.core.cache.core.field.TableField;
import ru.said.miami.orm.core.cache.core.table.TableInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by said on 18.03.17.
 */
public class QueryBuilder<T> {

    private Where where;
    private int limit;
    private ORDER_BY orderBy;
    private Class<T> dbTable;
    private Dao<T> dao;

    public QueryBuilder(Class<T> dbTable, Dao<T> dao) {
        this.dbTable = dbTable;
        this.dao = dao;
    }

    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;

        return this;
    }

    public QueryBuilder<T> orderBy(ORDER_BY orderBy) {
        this.orderBy = orderBy;

        return this;
    }

    public QueryBuilder<T> where(Where where) {
        this.where = where;

        return this;
    }

    public List<T> executeQuery() throws SQLException {
        return dao.queryForAll(getStringQuery());
    }

    public String getStringQuery() {
        StringBuilder sb = new StringBuilder();
        TableInfo tableInfo = new TableInfo(dbTable);

        sb
                .append("SELECT ")
                .append(((TableField) tableInfo.getId().getAnnotation(TableField.class)).fieldName())
                .append(" FROM ")
                .append(tableInfo.getTableName())
                .append(where == null ? "" : where.getStringQuery())
                .append(orderBy == null ? "" : " ORDER BY " + orderBy)
                .append(limit == 0 ? "" : " LIMIT " + limit);

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Query{" +
                "where=" + where +
                ", limit=" + limit +
                ", orderBy=" + orderBy +
                ", dbTable=" + dbTable +
                ", ru.said.miami.cache.core.dao=" + dao +
                '}';
    }

    public enum ORDER_BY {
        ASC,
        DESC
    }
}
