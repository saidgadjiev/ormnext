package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.criteria.impl.DeleteStatement;
import ru.saidgadjiev.orm.next.core.criteria.impl.SelectStatement;
import ru.saidgadjiev.orm.next.core.criteria.impl.UpdateStatement;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */
public interface Session<T, ID> extends BaseDao<T, ID> {
    TransactionImpl<T, ID> transaction() throws SQLException;

    SelectStatement<T> selectQuery();

    UpdateStatement<T> updateQuery();

    DeleteStatement<T> deleteQuery();
}
