package ru.said.miami.orm.core;

import org.sqlite.SQLiteDataSource;
import ru.said.miami.orm.core.dao.Dao;
import ru.said.miami.orm.core.dao.DaoManager;
import ru.said.miami.orm.core.queryBuilder.PreparedQuery;
import ru.said.miami.orm.core.queryBuilder.QueryBuilder;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:C:/test.sqlite");
        Dao<Account, Integer> accountDao = DaoManager.createDAO(dataSource, Account.class);
        Dao<Order, Integer> orderDao = DaoManager.createDAO(dataSource, Order.class);
        //accountDao.caching(true, null);
        //System.out.println("account created = " + orderDao.createTable(true));
        //System.out.println("account created = " + accountDao.createTable(true));
        QueryBuilder<Account> queryBuilder = accountDao.queryBuilder();

        PreparedQuery preparedQuery = queryBuilder.selectColumns()
                .function(queryBuilder.function().column("id").sum())
                .build()
                .where()
                .eq("id")
                .build()
                .leftJoin(orderDao.queryBuilder())
                .eq("id", "id")
                .build()
                .groupBy("id")
                .having()
                .eq(queryBuilder.function().column("id").sum(), 1000)
                .build()
                .prepare();

        accountDao.query(preparedQuery);

        //System.out.println("order created = " + orderDao.createTable());
        /*Account account = new Account();

        account.setName("account_name");
        accountDao.create(account);
        Order order = new Order();

        order.setName("test_order");
        order.setAccount(account);
        orderDao.create(order);
        Order order1 = new Order();

        order1.setName("test_order1");
        order1.setAccount(account);
        orderDao.create(order1);
        //List<Account> accounts = accountDao.query(accountDao.queryBuilder().where().eq("id", 24).prepare());

        System.out.println("account = " + accountDao.queryForId(1));*/
        //System.out.println("account = " + accountDao.queryForId(2));
    }

    public static Order getTestObject() {
        Order order = new Order();

        order.setName("test_name");

        return order;
    }
}
