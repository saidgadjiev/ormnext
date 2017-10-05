package ru.said.miami.orm.core.query.core;

import java.sql.SQLException;
import java.util.Date;

public interface IMiamiData {
    boolean getBoolean(String name)  throws SQLException;

    int getInt(String name) throws SQLException;

    String getString(String name) throws SQLException;

    Date getTime(String name) throws SQLException;

    double getDouble(String name) throws SQLException;

    Object getObject(String name) throws SQLException;
}
