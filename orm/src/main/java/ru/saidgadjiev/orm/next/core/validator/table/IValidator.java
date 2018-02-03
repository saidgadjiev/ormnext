package ru.saidgadjiev.orm.next.core.validator.table;

public interface IValidator {

    <T> void validate(Class<T> tClass) throws IllegalAccessException;
}
