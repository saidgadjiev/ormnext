package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.ForeignCollectionField;
import ru.saidgadjiev.orm.next.core.field.ForeignColumn;

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

    public static Optional<IDatabaseColumnType> create(Field field) {
        if (field.isAnnotationPresent(DatabaseColumn.class)) {
            return Optional.of(new DatabaseColumnTypeFactory().createFieldType(field));
        } else if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.of(new ForeignCollectionColumnTypeFactory().createFieldType(field));
        } else if (field.isAnnotationPresent(ForeignColumn.class)) {
            return Optional.of(new ForeignColumnTypeFactory().createFieldType(field));
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
                .filter(field -> field.isAnnotationPresent(DatabaseColumn.class) && field.getType() == type)
                .findFirst();
    }
}
