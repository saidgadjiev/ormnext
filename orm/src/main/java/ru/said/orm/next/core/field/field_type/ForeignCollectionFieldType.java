package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.persisters.DataPersister;
import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.ForeignCollectionField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class ForeignCollectionFieldType implements IDBFieldType {

    private Field field;

    private Class<?> foreignFieldClass;

    private String foreignFieldName;

    private Field foreignField;

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public boolean isNotNull() {
        return false;
    }

    @Override
    public boolean isGenerated() {
        return false;
    }

    @Override
    public String getColumnName() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public DataType getDataType() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public DataPersister getDataPersister() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("");
    }

    public Field getField() {
        return field;
    }

    @Override
    public int getLength() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isForeignCollectionFieldType() {
        return true;
    }

    public void add(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            ((Collection<Object>) field.get(object)).add(value);
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
            fieldType.foreignField = findFieldByType(field.getDeclaringClass(), fieldType.getForeignFieldClass()).orElseThrow(() -> new NoSuchFieldException("Foreign field is not defined"));
        } else {
            fieldType.foreignField = findFieldByName(fieldType.foreignFieldName, fieldType.foreignFieldClass);
        }

        return fieldType;
    }

    private static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }

    private static Field findFieldByName(String foreignFieldName, Class<?> clazz) throws NoSuchFieldException {
        return clazz.getDeclaredField(foreignFieldName);
    }

    private static Optional<Field> findFieldByType(Class<?> type, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(DBField.class) && field.getType() == type)
                .findFirst();
    }

    public static class ForeignCollectionFieldTypeCache {

        private static final Cache<Field, ForeignCollectionFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

        public static ForeignCollectionFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
            if (CACHE.contains(field)) {
                return CACHE.get(field);
            }
            ForeignCollectionFieldType fieldType = ForeignCollectionFieldType.build(field);

            CACHE.put(field, fieldType);

            return fieldType;
        }
    }
}
