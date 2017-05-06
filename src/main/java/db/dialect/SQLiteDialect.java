package db.dialect;

/**
 * Created by said on 02.05.17.
 */
public class SQLiteDialect implements IDialect {

    public String lastInsertId() {
        return "last_insert_rowid()";
    }
}
