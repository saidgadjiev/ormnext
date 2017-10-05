package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<Test, Integer> dao = DaoManager.createDAO(dataSource, Test.class);
        Test test = getTestObject();
        List<Test> result = dao.queryForAll();

        System.out.println("result = " + result.toString());
    }

    public static Test getTestObject() {
        Test test = new Test();

        test.setName("test_name");

        return test;
    }
}
