package dao;

import clause.Update;
import clause.Where;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public interface Dao<T> {

    void create(T object) throws SQLException;

    T queryForId(long id) throws SQLException;

    List<T> queryForAll() throws SQLException;

    void update(T object);

    void delete(T object);

    List<T> queryForWhere(Where where) throws SQLException;

    int queryForUpdate(Update update) throws SQLException;

    boolean deleteForWhere(Where where) throws SQLException;
}
