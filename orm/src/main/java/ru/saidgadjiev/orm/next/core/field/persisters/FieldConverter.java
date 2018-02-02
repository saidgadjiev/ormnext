package ru.saidgadjiev.orm.next.core.field.persisters;

public interface FieldConverter<T> {

    T convertTo(String value) throws IllegalArgumentException;
}
