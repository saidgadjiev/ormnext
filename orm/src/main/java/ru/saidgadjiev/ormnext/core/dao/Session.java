package ru.saidgadjiev.ormnext.core.dao;

import java.sql.SQLException;

/**
 * Created by said on 19.02.2018.
 */

//TODO: переименовать в Session так как Session как таковой нету тут. Поставить размер кеша для сессии 32
public interface Session extends AutoCloseable, BaseDao {

    void beginTransaction() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
