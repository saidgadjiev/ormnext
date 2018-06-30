package ru.saidgadjiev.ormnext.core.utils;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * Provide methods for create {@link DatabaseColumnType} from field.
 *
 * @author Said Gadjiev
 */
public final class FieldTypeUtils {

    /**
     * Util class cen't be instantiated.
     */
    private FieldTypeUtils() { }

    /**
     * Create one of the {@link DatabaseColumnType} implementations from field.
     * @param field target field
     * @return database column type
     */
    public static Optional<DatabaseColumnType> create(Field field) {
        if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.ofNullable(ForeignCollectionColumnTypeImpl.build(field));
        } else if (field.isAnnotationPresent(ForeignColumn.class)) {
            return Optional.ofNullable(ForeignColumnTypeImpl.build(field));
        } else if (field.isAnnotationPresent(DatabaseColumn.class)) {
            return Optional.ofNullable(SimpleDatabaseColumnTypeImpl.build(field));
        }

        return Optional.empty();
    }

    /**
     * Resolve field generic type. Use for collection type fields.
     * @param field target field
     * @return generic field class
     */
    public static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }

    /**
     * Find field which name is {@code foreignFieldName} in requested class.
     * @param foreignFieldName target field name
     * @param clazz target class
     * @return field
     */
    public static Optional<Field> findFieldByName(String foreignFieldName, Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredField(foreignFieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    /**
     * Find field which type is {@code type} in requested class.
     * @param type target field type
     * @param clazz target class
     * @return field
     */
    public static Optional<Field> findFieldByType(Class<?> type, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ForeignColumn.class) && field.getType() == type)
                .findFirst();
    }

    /**
     * Resolve foreign column name by field.
     * @param field target field
     * @return foreign field name
     */
    public static String resolveForeignColumnTypeName(Field field) {
        ForeignColumn foreignColumn = field.getAnnotation(ForeignColumn.class);

        String columnName = foreignColumn.columnName().isEmpty()
                ? field.getName().toLowerCase() : foreignColumn.columnName();

        return columnName + ForeignColumnTypeImpl.ID_SUFFIX;
    }

    /**
     * Resolve column name by field.
     *
     * @param field target field
     * @return field column name
     */
    public static Optional<String> resolveColumnName(Field field) {
        if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.empty();
        } else if (field.isAnnotationPresent(ForeignColumn.class)) {

            ForeignColumn foreignColumn = field.getAnnotation(ForeignColumn.class);

            String columnName = foreignColumn.columnName().isEmpty()
                    ? field.getName().toLowerCase() : foreignColumn.columnName();

            if (columnName.endsWith(ForeignColumnTypeImpl.ID_SUFFIX)) {
                return Optional.of(columnName);
            }

            return Optional.of(columnName + ForeignColumnTypeImpl.ID_SUFFIX);
        } else if (field.isAnnotationPresent(DatabaseColumn.class)) {
            DatabaseColumn databaseColumn = field.getAnnotation(DatabaseColumn.class);

            String columnName = databaseColumn.columnName().isEmpty()
                    ? field.getName().toLowerCase() : databaseColumn.columnName();

            return Optional.of(columnName);
        }

        return Optional.empty();
    }
}
