package dao;

import java.sql.SQLException;

/**
 * Created by said on 25.02.17.
 */
public interface Dao<T> {

    void create(T object) throws SQLException;
}
