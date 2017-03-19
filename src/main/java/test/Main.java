package test;

import clause.QueryBuilder;
import clause.Where;
import dao.Dao;
import dao.DaoImpl;
import db.SQLiteConnectionSource;
import org.apache.log4j.Logger;
import support.JDBCConnectionSource;
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
        Dao<Test> daoTest = new DaoImpl<>(connectionSource, Test.class);

        Test test = daoTest.queryForId(1);
        System.out.println(test);
    }
}
