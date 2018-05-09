package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

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
            return Optional.ofNullable(DatabaseColumnType.build(field));
        } else if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.ofNullable(ForeignCollectionColumnType.build(field));
        } else if (field.isAnnotationPresent(ForeignColumn.class)) {
            return Optional.ofNullable(ForeignColumnType.build(field));
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
                .filter(field -> field.isAnnotationPresent(ForeignColumn.class) && field.getType() == type)
                .findFirst();
    }

    public static String resolveForeignColumnTypeName(Field field) {
        ForeignColumn foreignColumn = field.getAnnotation(ForeignColumn.class);

        String columnName = foreignColumn.columnName().isEmpty() ? field.getName().toLowerCase() : foreignColumn.columnName();

        return columnName + ForeignColumnType.ID_SUFFIX;
    }
}
