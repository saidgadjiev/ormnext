package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown if impossible access to field.
 *
 * @author said gadjiev
 */
public class FieldAccessException extends RuntimeException {

    /**
     * Constructs a new instance with the specified clause.
     * @param clause target clause
     */
    public FieldAccessException(Throwable clause) {
        super(clause);
    }
}
