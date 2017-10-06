package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<Order, Integer> dao = DaoManager.createDAO(dataSource, Order.class);

        System.out.println("result = " + dao.createTable());
    }

    public static Order getTestObject() {
        Order order = new Order();

        order.setName("test_name");

        return order;
    }
}
