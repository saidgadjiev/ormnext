package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.fieldTypes.DBFieldType;
import ru.said.miami.orm.core.stamentExecutor.PreparedQueryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by said on 12.11.17.
 */
public class PreparedQuery {

    private Map<Integer, Object> args = new HashMap<>();

    private String sql;

    private List<DBFieldType> resultFieldTypes;

    public PreparedQuery(String sql, List<DBFieldType> resultFieldTypes) {
        this.sql = sql;
        this.resultFieldTypes = resultFieldTypes;
    }

    public PreparedQuery(Map<Integer, Object> args, String sql, List<DBFieldType> resultFieldTypes) {
        this.args.putAll(args);
        this.sql = sql;
        this.resultFieldTypes = resultFieldTypes;
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

    public List<DBFieldType> getResultFieldTypes() {
        return resultFieldTypes;
    }

    @Override
    public String toString() {
        return "PreparedQuery{" +
                "sql='" + sql + '\'' +
                "args='" + args.toString() + "'" +
                '}';
    }
}
