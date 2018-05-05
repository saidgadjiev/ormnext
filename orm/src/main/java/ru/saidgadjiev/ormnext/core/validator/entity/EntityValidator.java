package ru.saidgadjiev.ormnext.core.validator.entity;

public class EntityValidator implements IValidator {
    @Override
    public <T> void validate(Class<T> tClass) {
        new ForeignKeyValidator().validate(tClass);
        new HasConstructorValidator().validate(tClass);
        new PrimaryKeyValidator().validate(tClass);
    }
}
