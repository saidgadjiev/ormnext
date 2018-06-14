package ru.saidgadjiev.ormnext.core.exception;

import ru.saidgadjiev.ormnext.core.field.SqlType;

/**
 * Exception will be thrown when column type not defined.
 *
 * @author Said Gadjiev
 */
public class UnknownTypeException extends RuntimeException {

    /**
     * Constructs a new instance with the specified type.
     *
     * @param type        target type
     */
    public UnknownTypeException(SqlType type) {
        super("Type " + type.name() + " unknown");
    }
}
