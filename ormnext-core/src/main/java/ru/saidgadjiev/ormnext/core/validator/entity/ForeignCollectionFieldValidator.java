package ru.saidgadjiev.ormnext.core.validator.entity;

import ru.saidgadjiev.ormnext.core.exception.ForeignFieldForForeignCollectionNotFoundException;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.utils.FieldTypeUtils;

import java.lang.reflect.Field;

/**
 * Validate foreign collection field eg. check is foreign field defined.
 *
 * @author Said Gadjiev
 */
public class ForeignCollectionFieldValidator implements Validator {

    @Override
    public <T> void validate(Class<T> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ForeignCollectionField.class)) {
                ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
                Class<?> collectionObjectClass = FieldTypeUtils.getCollectionGenericClass(field);

                if (foreignCollectionField.foreignFieldName().isEmpty()) {
                    FieldTypeUtils.findFieldByType(
                            field.getDeclaringClass(),
                            collectionObjectClass
                    ).orElseThrow(
                            () -> new ForeignFieldForForeignCollectionNotFoundException(collectionObjectClass, field)
                    );
                } else {
                    FieldTypeUtils.findFieldByName(
                            foreignCollectionField.foreignFieldName(),
                            collectionObjectClass
                    ).orElseThrow(
                            () -> new ForeignFieldForForeignCollectionNotFoundException(
                                    foreignCollectionField.foreignFieldName(),
                                    collectionObjectClass,
                                    field
                            )
                    );
                }
            }
        }
    }
}
