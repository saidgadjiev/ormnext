package ru.saidgadjiev.orm.next.core.field.field_type;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.ForeignCollectionField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by said on 21.01.2018.
 */
public class FieldTypeUtils {

    private FieldTypeUtils() {

    }

    public static Optional<IDBFieldType> create(Field field) {
        if (field.isAnnotationPresent(DBField.class)) {
            DBField dbField = field.getAnnotation(DBField.class);

            if (dbField.foreign()) {
                return Optional.of(new ForeignFieldTypeFactory().createFieldType(field));
            } else {
                return Optional.of(new DBFieldTypeFactory().createFieldType(field));
            }
        } else if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.of(new ForeignCollectionFieldTypeFactory().createFieldType(field));
        }

        return Optional.empty();
    }

    public static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }

    public static Field findFieldByName(String foreignFieldName, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(foreignFieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Optional<Field> findFieldByType(Class<?> type, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(DBField.class) && field.getType() == type)
                .findFirst();
    }
}
