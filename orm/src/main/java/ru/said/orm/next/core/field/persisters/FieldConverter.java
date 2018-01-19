package ru.said.orm.next.core.field.persisters;

import java.sql.SQLException;

public interface FieldConverter<T> {

    T convertTo(String value) throws IllegalArgumentException;
}
