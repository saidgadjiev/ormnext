package ru.saidgadjiev.orm.next.core.support;

import java.sql.SQLException;

public interface OrmNextResultSet {

    boolean next() throws SQLException;

    Object getObject(String columnName) throws SQLException;

    Object getObject(int columnId) throws SQLException;

    OrmNextMetaData getMetaData();

    GeneratedKeys generatedKeys();

    interface OrmNextMetaData {
        int getColumnType(int columnId) throws SQLException;
    }

    interface GeneratedKeys {
        boolean next() throws SQLException;

        Object getGeneratedKey() throws SQLException;

        int getType() throws SQLException;
    }
}
