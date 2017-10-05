package ru.said.miami.orm.core.query.core;

import java.sql.SQLException;

public interface IMiamiCollection extends AutoCloseable {
    boolean next() throws SQLException;

    IMiamiData get() throws SQLException;

    void close() throws SQLException;
}
