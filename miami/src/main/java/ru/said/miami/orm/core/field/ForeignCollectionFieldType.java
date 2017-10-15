package ru.said.miami.orm.core.field;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

public class ForeignCollectionFieldType {

    private Field field;

    private Class<?> foreignFieldClass;

    private String foreignFieldName;

    private Field foreignField;

    public Field getField() {
        return field;
    }

    public void add(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            ((Collection) field.get(object)).add(value);
            field.setAccessible(false);
        } else {
            ((Collection) field.get(object)).add(value);
        }
    }

    public void addAll(Object object, Collection<?> value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            ((Collection) field.get(object)).addAll(value);
            field.setAccessible(false);
        } else {
            ((Collection) field.get(object)).addAll(value);
        }
    }

    public Field getForeignField() {
        return foreignField;
    }

    public Class<?> getForeignFieldClass() {
        return foreignFieldClass;
    }

    public static ForeignCollectionFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
        ForeignCollectionFieldType fieldType = new ForeignCollectionFieldType();

        fieldType.field = field;
        fieldType.foreignFieldName = foreignCollectionField.foreignFieldName();
        fieldType.foreignFieldClass = getCollectionGenericClass(field);

        if (fieldType.foreignFieldName.isEmpty()) {
            fieldType.foreignField = findFieldByType(field.getDeclaringClass(), fieldType.getForeignFieldClass());
        } else {
            fieldType.foreignField = findFieldByName(fieldType.foreignFieldName, fieldType.foreignFieldClass);
        }

        return fieldType;
    }

    private static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type [] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }

    private static Field findFieldByName(String foreignFieldName, Class<?> clazz) throws NoSuchFieldException {
        return clazz.getDeclaredField(foreignFieldName);
    }

    private static Field findFieldByType(Class<?> type, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
            if (field.isAnnotationPresent(DBField.class)) {
                if (field.getType() == type) {
                    return true;
                }
            }

            return false;
        }).findFirst().orElseGet(null);
    }
}
