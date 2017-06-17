package utils;

/**
 * Created by said on 12.06.17.
 */
public class PlatformUtils {

    public PlatformUtils() {

    }

    public String determineDatabaseType(String jdbcConnectionUrl) {
        String erasedJDBCPrefix = jdbcConnectionUrl.substring(5, jdbcConnectionUrl.length());

        return erasedJDBCPrefix.substring(0, erasedJDBCPrefix.indexOf(":"));
    }
}
