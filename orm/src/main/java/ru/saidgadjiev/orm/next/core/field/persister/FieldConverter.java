package ru.saidgadjiev.orm.next.core.field.persister;

import ru.saidgadjiev.orm.next.core.field.field_type.IDatabaseColumnType;

public interface FieldConverter<T> {

    T parseDefaultTo(IDatabaseColumnType fieldType, String value) throws IllegalArgumentException;

    default Object parseSqlToJava(IDatabaseColumnType fieldType, Object object) throws Exception {
        return object;
    }

    default Object parseJavaToSql(IDatabaseColumnType fieldType, Object object) {
        return object;
    }
}
