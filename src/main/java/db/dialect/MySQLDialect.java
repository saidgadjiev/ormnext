package db.dialect;

/**
 * Created by said on 02.05.17.
 */
public class MySQLDialect implements IDialect {

    @Override
    public String lastInsertId() {
        return "LAST_INSERT_ID()";
    }
}
