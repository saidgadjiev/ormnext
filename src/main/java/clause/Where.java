package clause;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 11.03.17.
 */
public class Where {

    private Map<String, Object> eq = new HashMap<String, Object>();

    public void addEqClause(String name, Object value) {
        eq.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("WHERE ");
        for (Map.Entry<String, Object> entry: eq.entrySet()) {
            sb.append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'").append(" AND ");
        }
        sb.replace(sb.length() - 5, sb.length(), "");

        return sb.toString();
    }
}
