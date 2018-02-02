package ru.saidgadjiev.orm.next.core.table.validators;

public interface IValidator {

    <T> void validate(Class<T> tClass) throws IllegalAccessException;
}
