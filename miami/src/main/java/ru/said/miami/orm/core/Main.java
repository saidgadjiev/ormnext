package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<Account, Integer> accountDao = DaoManager.createDAO(dataSource, Account.class);
        //Dao<Order, Integer> orderDao = DaoManager.createDAO(dataSource, Order.class);
        //System.out.println("account created = " + accountDao.createTable());
        //System.out.println("order created = " + orderDao.createTable());
       /* Order order = new Order();

        order.setName("test_order");
        Account account = new Account();

        account.setName("account_name");
        account.setOrder(order);
        accountDao.create(account);*/
        List<Account> accounts = accountDao.queryForAll();

        System.out.println("accounts = " + accounts);
    }

    public static Order getTestObject() {
        Order order = new Order();

        order.setName("test_name");

        return order;
    }
}
