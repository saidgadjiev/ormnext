package ru.saidgadjiev.ormnext.core.validator.entity;

/**
 * Database entity validator.
 *
 * @author Said Gadjiev
 */
public class EntityValidator implements Validator {

    @Override
    public <T> void validate(Class<T> entityClass) {
        new HasDefaultConstructorValidator().validate(entityClass);
        new PrimaryKeyValidator().validate(entityClass);
        new ForeignCollectionFieldValidator().validate(entityClass);
    }
}
