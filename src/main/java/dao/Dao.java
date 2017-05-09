package dao;

import clause.QueryBuilder;
import clause.Update;
import clause.Where;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public interface Dao<T> {

    void create(T object) throws SQLException;

    T queryForId(int id) throws SQLException;

    List<T> queryForAll() throws SQLException;

    List<T> queryForAll(String sql) throws SQLException;

    void update(T object);

    void delete(T object);

    List<T> queryForWhere(Where where) throws SQLException;

    int queryForUpdate(Update update) throws SQLException;

    boolean deleteForWhere(Where where) throws SQLException;

    QueryBuilder<T> queryBuilder();
}
