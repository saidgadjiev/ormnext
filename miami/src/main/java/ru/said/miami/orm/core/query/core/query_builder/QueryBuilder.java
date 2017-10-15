package ru.said.miami.orm.core.query.core.query_builder;

import ru.said.miami.orm.core.query.core.IMiamiCollection;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.query.core.SelectQuery;
import ru.said.miami.orm.core.query.core.object_builder.ObjectBuilderChain;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 14.10.17.
 */
public class QueryBuilder<T> {

    private SelectQuery query;

    private DataSource dataSource;

    private TableInfo<T> tableInfo;

    private Where where;

    private ObjectBuilderChain<T> objectBuilderChain;

    public QueryBuilder(DataSource dataSource, TableInfo<T> tableInfo) {
        this.dataSource = dataSource;
        this.tableInfo = tableInfo;
        this.where = new Where();
        this.objectBuilderChain = new ObjectBuilderChain<>(dataSource, tableInfo);
        this.query = SelectQuery.buildQueryForAll(tableInfo.getTableName());
    }

    public Where where() {
        return where;
    }

    public List<T> execute() throws SQLException {
        List<T> resultList = new ArrayList<>();

        query.setWhere(where.getWhere());
        try (Connection connection = dataSource.getConnection()) {
            try (IMiamiCollection result = query.execute(connection)) {
                while (result.next()) {
                    IMiamiData miamiData = result.get();

                    resultList.add(objectBuilderChain.build(miamiData));
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultList;
    }

}
