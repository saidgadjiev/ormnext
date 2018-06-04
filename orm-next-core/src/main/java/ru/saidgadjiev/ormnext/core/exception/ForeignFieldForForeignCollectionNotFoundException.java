package ru.saidgadjiev.ormnext.core.exception;

import java.lang.reflect.Field;

/**
 * Exception will be thrown if foreign field not found for foreign collection field in foreign table.
 *
 * @author said gadjiev
 */
public class ForeignFieldForForeignCollectionNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance with the specified field.
     *
     * @param foreignCollectionClass target foreign class
     * @param collectionField        target collection field
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

    /**
     * Constructs a new instance with the specified field.
     *
     * @param fieldName              target foreign field name
     * @param foreignCollectionClass target foreign class
     * @param collectionField        target collection field
     */
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
