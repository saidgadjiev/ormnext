package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FieldAccessor {

    private Log log = LoggerFactory.getLogger(FieldAccessor.class);

    private Field field;

    private Method setter;

    private Method getter;

    public FieldAccessor(Field field) {
        this.field = field;
        resolveGetter(field);
        resoveSetter(field);
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        if (setter != null) {
            assignBySetter(object, value);
        } else {
            assignByField(object, value);
        }
    }

    private void assignByField(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } else {
            field.set(object, value);
        }
    }

    private void assignBySetter(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
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

    private void resolveGetter(Field field) {
        if (field.isAnnotationPresent(Getter.class)) {
            String getterName = field.getAnnotation(Getter.class).name();
            log.debug("Try find method " + getterName + " in " + field.getDeclaringClass().getName());

            try {
                this.getter = field.getDeclaringClass().getDeclaredMethod(getterName, field.getType());
                log.debug("Resolved getter " + getterName + " for " + field.getDeclaringClass().getName() + " " + field.getName());
            } catch (NoSuchMethodException ex) {
                log.error("Method " + getterName + " not found in " + field.getDeclaringClass().getName() + " use right access to field");
            }
        } else {
            Class<?> declaringClass = field.getDeclaringClass();
            String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            log.debug("Try find method " + getterName + " in " + field.getDeclaringClass().getName());

            try {
                this.getter = declaringClass.getDeclaredMethod(getterName);
                log.debug("Resolved getter " + getterName + " for " + field.getDeclaringClass().getName() + " " + field.getName());
            } catch (NoSuchMethodException ex) {
                log.error("Method " + getterName + " not found in " + field.getDeclaringClass().getName() + " use right access to field");
            }
        }
    }

    private void resoveSetter(Field field) {
        if (field.isAnnotationPresent(Setter.class)) {
            String setterName = field.getAnnotation(Setter.class).name();
            log.debug("Try find method " + setterName + " in " + field.getDeclaringClass().getName());

            try {
                this.setter = field.getDeclaringClass().getDeclaredMethod(setterName, field.getType());
                log.debug("Resolved setter " + setterName + " for " + field.getDeclaringClass().getName() + " " + field.getName());
            } catch (NoSuchMethodException ex) {
                log.error("Method " + setterName + " not found in " + field.getDeclaringClass().getName() + " use right access to field");
            }
        } else {
            Class<?> declaringClass = field.getDeclaringClass();
            String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            log.debug("Try find method " + setterName + " in " + field.getDeclaringClass().getName());

            try {
                this.setter = declaringClass.getDeclaredMethod(setterName, field.getType());
                log.debug("Resolved setter " + setterName + " for " + field.getDeclaringClass().getName() + " " + field.getName());
            } catch (NoSuchMethodException ex) {
                log.error("Method " + setterName + " not found in " + field.getDeclaringClass().getName() + " use right access to field");
            }
        }
    }
}
