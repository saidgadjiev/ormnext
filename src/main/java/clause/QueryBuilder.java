package clause;

import dao.Dao;
import utils.TableUtils;

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

        sb.append("SELECT * FROM ").append("test").append(" ");
        sb.append(where == null ? "": where.getStringQuery()).append(" ");
        sb.append("ORDER BY ").append(orderBy).append(" ");
        sb.append("LIMIT ").append(limit == 0 ? "": limit);

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Query{" +
                "where=" + where +
                ", limit=" + limit +
                ", orderBy=" + orderBy +
                ", dbTable=" + dbTable +
                ", dao=" + dao +
                '}';
    }

    public enum ORDER_BY {
        ASC,
        DESC
    }
}
