package ru.saidgadjiev.ormnext.core.validator.entity;

public interface IValidator {

    <T> void validate(Class<T> tClass);
}
