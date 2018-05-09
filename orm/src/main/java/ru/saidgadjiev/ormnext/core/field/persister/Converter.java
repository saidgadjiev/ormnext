package ru.saidgadjiev.ormnext.core.field.persister;

import java.sql.SQLException;

public interface Converter<T, R> {

    T javaToSql(R value) throws SQLException;

    R sqlToJava(T value) throws SQLException;
}
