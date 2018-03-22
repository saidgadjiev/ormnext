package ru.saidgadjiev.orm.next.core.dao;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */
public interface Session extends BaseDao {
    TransactionImpl transaction() throws SQLException;
}
