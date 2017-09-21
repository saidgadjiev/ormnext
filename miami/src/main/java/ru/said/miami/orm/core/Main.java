package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<Test, String> dao = DaoManager.createDAO(dataSource, Test.class);
        Test test = getTestObject();
        int result = dao.create(test);

        System.out.println("result = " + result);
    }

    public static Test getTestObject() {
        Test test = new Test();

        test.setName("test_name");

        return test;
    }
}
