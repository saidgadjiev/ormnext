package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;

public interface FieldConverter<T> {

    T parseDefaultTo(IDBFieldType fieldType, String value) throws IllegalArgumentException;

    default Object parseSqlToJava(IDBFieldType fieldType, Object object) throws Exception {
        return object;
    }

    default Object parseJavaToSql(IDBFieldType fieldType, Object object) {
        return object;
    }
}
