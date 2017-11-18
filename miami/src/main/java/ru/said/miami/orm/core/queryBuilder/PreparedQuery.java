package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.stamentExecutor.PreparedQueryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 12.11.17.
 */
public class PreparedQuery {

    private Map<Integer, Object> args = new HashMap<>();

    private String sql;

    public PreparedQuery(String sql) {
        this.sql = sql;
    }

    public PreparedQueryImpl compile(Connection connection) throws SQLException {
        PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(sql));

        for (Map.Entry<Integer, Object> entry: args.entrySet()) {
            preparedQuery.setObject(entry.getKey(), entry.getValue());
        }

        return preparedQuery;
    }

    public void setArg(int index, Object value) {
        args.put(index, value);
    }

}
