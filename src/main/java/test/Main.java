package test;

import dao.Dao;
import dao.DaoImpl;
import support.JDBCConnectionSource;
import db.SQLiteConnectionSource;
import org.apache.log4j.Logger;
import utils.TableUtils;

import java.sql.SQLException;

/**
 * Created by said on 25.02.17.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        JDBCConnectionSource connectionSource = new SQLiteConnectionSource("jdbc:sqlite:test.sqlite");
        Dao<Test> dao = new DaoImpl<Test>(connectionSource, Test.class);
        TableUtils.createTable(connectionSource, Test.class);
    }
}
