import dao.Dao;
import dao.DaoImpl;
import db.SQLiteConnectionSource;
import support.JDBCConnectionSource;

import java.sql.SQLException;

/**
 * Created by said on 09.05.17.
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        JDBCConnectionSource connectionSource = new SQLiteConnectionSource("jdbc:sqlite:test1.sqlite");
        Dao<Test> dao = new DaoImpl<>(connectionSource, Test.class);

        dao.queryForId(1);
        dao.queryForId(1);
        System.out.println("YES");
    }
}
