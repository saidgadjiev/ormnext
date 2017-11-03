package ru.said.miami.orm.core.table.validator;

import ru.said.miami.orm.core.field.DBField;

import java.lang.reflect.Field;

public class PrimaryKeyValidator implements IValidator {

    public<T> void validate(Class<T> tClass) throws IllegalAccessException {
        int idCount = 0;

        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);

                if (dbField.id()) {
                    ++idCount;
                }
                if (dbField.generated() && !dbField.id()) {
                    throw new IllegalAccessException("Field is generated but not id");
                }
            }
        }
        if (idCount > 1) {
            throw new IllegalAccessException("Many generated primary keys");
        }
    }
}
