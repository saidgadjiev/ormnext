package ru.said.miami.orm.core.table.validators;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.table.utils.TableInfoUtils;

import java.lang.reflect.Field;

public class ForeignKeyValidator implements IValidator {

    public <T> void validate(Class<T> tClass) throws IllegalAccessException {
        try {
            for (Field field : tClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(DBField.class)) {
                    DBField dbField = field.getAnnotation(DBField.class);

                    if (dbField.foreign() && !TableInfoUtils.resolvePrimaryKey(field.getType()).isPresent()) {
                        throw new IllegalAccessException("Foreign id is not defined");
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalAccessException(ex.getMessage());
        }
    }
}
