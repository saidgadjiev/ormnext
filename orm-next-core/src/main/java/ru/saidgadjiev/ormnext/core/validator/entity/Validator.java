package ru.saidgadjiev.ormnext.core.validator.entity;

/**
 * Interface for different entity validators.
 */
public interface Validator {

    /**
     * Validate some rule for entity class.
     * @param entityClass target entity class
     * @param <T> entity class type
     */
    <T> void validate(Class<T> entityClass);
}
