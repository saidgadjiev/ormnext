package ru.saidgadjiev.orm.next.core.examples.custom_type;

import org.h2.jdbcx.JdbcDataSource;
import ru.saidgadjiev.orm.next.core.dao.SessionManagerImpl;
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
        SessionManager sessionManager = new SessionManagerImpl(new PolledConnectionSource(dataSource, new H2DatabaseType()), null);
        Session session = sessionManager.getCurrentSession();

        session.createTable(Account.class, true);
        session.createTable(Order.class, true);
        Account account = new Account("Said");

        session.create(account);
        Order order = new Order("Тестовый заказ", account);

        session.create(order);
        Order persistedOrder = session.queryForId(Order.class, 1);

        System.out.println(persistedOrder.toString());
    }


}
