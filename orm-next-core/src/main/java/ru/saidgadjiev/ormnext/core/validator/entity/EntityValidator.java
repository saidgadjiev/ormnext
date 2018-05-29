package ru.saidgadjiev.ormnext.core.validator.entity;

/**
 * Database entity validator.
 */
public class EntityValidator implements IValidator {

    @Override
    public <T> void validate(Class<T> entityClass) {
        new HasDefaultConstructorValidator().validate(entityClass);
        new PrimaryKeyValidator().validate(entityClass);
    }
}
