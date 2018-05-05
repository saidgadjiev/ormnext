package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;

import java.sql.SQLException;

public interface FieldConverter<T> {

    T parseDefaultTo(IDatabaseColumnType fieldType, String value) throws IllegalArgumentException;

    default Object parseSqlToJava(IDatabaseColumnType fieldType, Object object) throws SQLException {
        return object;
    }

    default Object parseJavaToSql(IDatabaseColumnType fieldType, Object object) {
        return object;
    }
}
