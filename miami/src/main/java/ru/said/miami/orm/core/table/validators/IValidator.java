package ru.said.miami.orm.core.table.validators;

public interface IValidator {

    <T> void validate(Class<T> tClass) throws IllegalAccessException;
}
