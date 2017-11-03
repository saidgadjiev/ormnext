package ru.said.miami.orm.core.table.validator;

public interface IValidator {

    <T> void validate(Class<T> tClass) throws IllegalAccessException;
}
