package test;

import clause.Update;
import clause.Where;
import dao.Dao;
import dao.DaoImpl;
import support.JDBCConnectionSource;
import db.SQLiteConnectionSource;
import org.apache.log4j.Logger;
import utils.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        JDBCConnectionSource connectionSource = new SQLiteConnectionSource("jdbc:sqlite:test.sqlite");
        Dao<Test> dao = new DaoImpl<Test>(connectionSource, Test.class);
        Update update = new Update();
        Where where = new Where();

        where.addEqClause("test_name", "test");
        update.setWhere(where);
        update.addUpdateColumn("test_name", "test2");

        int count = dao.queryForUpdate(update);

        System.out.println(count);
    }
}
