package ru.said.orm.next.core.field;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FieldAccessor {

    private Field field;

    private Method setter;

    private Method getter;

    public FieldAccessor(Field field) throws NoSuchMethodException {
        this.field = field;
        this.setter = field.isAnnotationPresent(Setter.class) ? field.getDeclaringClass().getMethod(field.getAnnotation(Setter.class).name(), field.getType()) : null;
        this.getter = field.isAnnotationPresent(Getter.class) ? field.getDeclaringClass().getMethod(field.getAnnotation(Getter.class).name()) : null;
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        if (setter != null) {
            assignSetter(object, value);
        } else {
            assignField(object, value);
        }
    }

    private void assignField(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } else {
            field.set(object, value);
        }
    }

    private void assignSetter(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        setter.invoke(object, value);
    }

    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        if (getter != null) {
            return accessByGetter(object);
        } else {
            return accessByField(object);
        }
    }

    private Object accessByField(Object object) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            Object result = field.get(object);

            field.setAccessible(false);

            return result;
        }

        return field.get(object);
    }

    private Object accessByGetter(Object object) throws IllegalAccessException, InvocationTargetException {
        return getter.invoke(object);
    }
}
