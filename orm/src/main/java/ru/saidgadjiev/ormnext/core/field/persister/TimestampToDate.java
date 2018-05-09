package ru.saidgadjiev.ormnext.core.field.persister;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampToDate implements Converter<Timestamp, java.util.Date> {

    @Override
    public Timestamp javaToSql(java.util.Date value) throws SQLException {
        return new Timestamp(value.getTime());
    }

    @Override
    public java.util.Date sqlToJava(Timestamp value) throws SQLException {
        return new Date(value.getTime());
    }
}
