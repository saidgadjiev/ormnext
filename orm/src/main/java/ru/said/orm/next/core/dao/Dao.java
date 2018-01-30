package ru.said.orm.next.core.dao;

import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.stament_executor.GenericResults;
import ru.said.orm.next.core.stament_executor.ResultsMapper;
import ru.said.orm.next.core.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Класс для DAO
 * @param <T> тип объекта
 * @param <ID> тип id
 */
public interface Dao<T, ID> extends BaseDao<T, ID> {

    void caching(boolean flag);

    void setObjectCache(ObjectCache objectCache);

    void caching(boolean flag, ObjectCache objectCache);

    ConnectionSource getDataSource();

    TransactionImpl<T, ID> transaction() throws SQLException;
}
