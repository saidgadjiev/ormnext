package ru.saidgadjiev.orm.next.core.validator.table;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.utils.TableInfoUtils;

import java.lang.reflect.Field;

public class ForeignKeyValidator implements IValidator {

    public <T> void validate(Class<T> tClass) {
        for (Field field : tClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);

                if (dbField.foreign() && !TableInfoUtils.resolvePrimaryKey(field.getType()).isPresent()) {
                    throw new IllegalArgumentException("Foreign id is not defined");
                }
            }
        }
    }
}
