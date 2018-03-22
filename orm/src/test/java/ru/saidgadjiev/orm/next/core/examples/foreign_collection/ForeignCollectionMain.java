package ru.saidgadjiev.orm.next.core.examples.foreign_collection;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.h2.jdbcx.JdbcDataSource;
import ru.saidgadjiev.orm.next.core.criteria.impl.Projections;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.SessionManager;
import ru.saidgadjiev.orm.next.core.db.H2DatabaseType;
import ru.saidgadjiev.orm.next.core.db.MySQLDatabaseType;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stament_executor.GenericResults;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.support.PolledConnectionSource;

import java.util.List;

/**
 * Created by said on 27.02.2018.
 */
public class ForeignCollectionMain {

    public static void main(String[] args) throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setUser("root");
        dataSource.setPassword("said1995");
        dataSource.setURL("jdbc:mysql://localhost:3306/overtalk");
        SessionManager sessionManager = new BaseSessionManagerImpl(new PolledConnectionSource(dataSource, new MySQLDatabaseType()));
        Session accountDao = sessionManager.forClass(Account.class);
        Session orderDao = sessionManager.forClass(Order.class);

        accountDao.createTable(true);
        orderDao.createTable(true);

        Account account = new Account();

        account.setName("test");
        accountDao.create(account);
        SelectStatement<Long> selectStatement = new SelectStatement<>(Account.class);

        selectStatement.selectProjections(Projections.projectionList()
        .add(Projections.selectFunction(Projections.countStar(), "cnt")));

        try (GenericResults<Long> genericResults = accountDao.query(selectStatement)) {
            Long sum = genericResults.getFirstResult(results -> results.getLong("cnt"));

            System.out.println(sum);
        }
    }
}
