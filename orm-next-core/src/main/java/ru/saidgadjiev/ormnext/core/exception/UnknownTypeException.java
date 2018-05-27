package ru.saidgadjiev.ormnext.core.exception;

/**
 * Exception will be thrown when column type not defined.
 */
public class UnknownTypeException extends RuntimeException {

    /**
     * Constructs a new instance with the specified type.
     *
     * @param type        target type
     */
    public UnknownTypeException(int type) {
        super("Type " + type + " unknown");
    }
}
