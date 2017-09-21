package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:/Users/said/Desktop/test.sqlite");
        Dao<Test, String> dao = DaoManager.createDAO(dataSource, Test.class);
        Boolean result = dao.createTable();

        System.out.println("result = " + result);
    }
}
