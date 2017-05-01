package test;

import dao.Dao;
import dao.DaoImpl;
import db.SQLiteConnectionSource;
import org.apache.log4j.Logger;
import support.JDBCConnectionSource;
import utils.TableUtils;

/**
 * Created by said on 25.02.17.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        JDBCConnectionSource connectionSource = new SQLiteConnectionSource("jdbc:sqlite:test.sqlite");
        Dao<Test1> daoTest1 = new DaoImpl<>(connectionSource, Test1.class);
        Dao<Test> daoTest = new DaoImpl<>(connectionSource, Test.class);

        System.out.print("YES");
    }
}
