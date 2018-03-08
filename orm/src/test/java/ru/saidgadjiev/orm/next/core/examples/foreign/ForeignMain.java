package ru.saidgadjiev.orm.next.core.examples.foreign;

import org.h2.jdbcx.JdbcDataSource;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.SessionManager;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.support.PolledConnectionSource;

/**
 * Created by said on 27.02.2018.
 */
public class ForeignMain {

    public static void main(String[] args) throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL("jdbc:h2:mem:h2testdb;DB_CLOSE_DELAY=-1");
        SessionManager sessionManager = new BaseSessionManagerImpl(new PolledConnectionSource(dataSource, new H2DatabaseType()));
        Session<Account, Integer> accountDao = sessionManager.forClass(Account.class);
        Session<Order, Integer> orderDao = sessionManager.forClass(Order.class);

        accountDao.createTable(true);
        orderDao.createTable(true);
        Account account = new Account("Said");

        accountDao.create(account);
        Order order = new Order("Тестовый заказ", account);

        orderDao.create(order);
        Order persistedOrder = orderDao.queryForId(1);

        System.out.println(persistedOrder.toString());
    }


}
