package clause;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 11.03.17.
 */
public class Update {

    private Map<String, Object> updateColumns = new HashMap<>();
    private Where where;

    public void addUpdateColumn(String name, Object value) {
        updateColumns.put(name, value);
    }

    public void setWhere(Where where) {
        this.where = where;
    }

    public String getStringQuery() {
        if (updateColumns.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        sb.append(" SET ");
        for (Map.Entry<String, Object> entry: updateColumns.entrySet()) {
            sb.append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'").append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append(where == null ? "" : where.getStringQuery());

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Update{" +
                "updateColumns=" + updateColumns +
                ", where=" + where +
                '}';
    }
}
