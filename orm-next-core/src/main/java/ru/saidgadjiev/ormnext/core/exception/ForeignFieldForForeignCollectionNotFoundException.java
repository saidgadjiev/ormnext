package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

/**
 * Exception will be thrown if generated value not found in result set.
 *
 * @author said gadjiev
 */
public class ForeignFieldForForeignCollectionNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the specified field.
     */
    public ForeignFieldForForeignCollectionNotFoundException(Class<?> foreignCollectionClass,
                                                             Field collectionField) {
        super(
                "Foreign field with type "
                + collectionField.getDeclaringClass() + " not found in "
                + foreignCollectionClass + " for collection field "
                + collectionField.getDeclaringClass()
                + ":" + collectionField.getName()
        );
    }

    public ForeignFieldForForeignCollectionNotFoundException(String fieldName,
                                                             Class<?> foreignCollectionClass,
                                                             Field collectionField) {
        super(
                "Foreign field with name "
                        + fieldName + " not found in "
                        + foreignCollectionClass + " for collection field "
                        + collectionField.getDeclaringClass()
                        + ":" + collectionField.getName()
        );
    }
}
