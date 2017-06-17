package dao.cache;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by said on 08.05.17.
 */
public class CacheResultSet {

    private Map<Integer, Map<String, Object>> cacheData = new LinkedHashMap<>();
    private Iterator<Map.Entry<Integer, Map<String, Object>>> iterator;
    private Map.Entry<Integer, Map<String, Object>> currEntry;

    public CacheResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        List<String> columnNames = new ArrayList<>();

        for (int i = 1; i <= resultSetMetaData.getColumnCount(); ++i) {
            columnNames.add(resultSetMetaData.getColumnName(i));
        }

        while (resultSet.next()) {
            Map<String, Object> data = new HashMap<>();

            for (String name: columnNames) {
                data.put(name, resultSet.getObject(name));
            }
            cacheData.put(resultSet.getRow(), data);
        }
        iterator = cacheData.entrySet().iterator();
    }

    public boolean next() {
        boolean next = iterator.hasNext();

        if (next) {
            currEntry = iterator.next();
        }

        return next;
    }

    public Long getLong(String name) {
        return currEntry != null ? (Long) currEntry.getValue().get(name) : null;
    }

    public Object getObject(String name) {
        return currEntry.getValue().get(name);
    }
}
