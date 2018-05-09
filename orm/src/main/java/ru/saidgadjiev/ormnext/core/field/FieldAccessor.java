package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldAccessor {

    private Log log = LoggerFactory.getLogger(FieldAccessor.class);

    private Field field;

    private BiConsumer setter;

    private Function getter;

    public FieldAccessor(Field field) {
        this.field = field;
        resolveGetter(field);
        resoveSetter(field);
    }

    public void assign(Object object, Object value) throws Throwable {
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

    private void assignBySetter(Object object, Object value) {
        setter.accept(object, value);
    }

    public Object access(Object object) throws Throwable {
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

    private Object accessByGetter(Object object) {
        return getter.apply(object);
    }

    private void resolveGetter(Field field) {
        String getterName;

        if (field.isAnnotationPresent(Getter.class)) {
            getterName = field.getAnnotation(Getter.class).name();
        } else {
            getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        ////LOG.debug("Try find method " + getterName + " in " + field.getDeclaringClass().getName());

        try {
            Method getter = field.getDeclaringClass().getDeclaredMethod(getterName);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle getterHandle = MethodHandles.lookup().unreflect(getter);

            this.getter = (Function) LambdaMetafactory.metafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    getterHandle,
                    getterHandle.type()
            ).getTarget().invokeExact();

            ////LOG.debug("Resolved getter " + getterName + " for " + field.getDeclaringClass().getName() + " " + field.getName());
        } catch (Throwable ex) {
           // //LOG.error("Method " + getterName + " not found in " + field.getDeclaringClass().getName() + " use right access to field");
        }
    }

    private void resoveSetter(Field field) {
        String setterName;

        if (field.isAnnotationPresent(Setter.class)) {
            setterName = field.getAnnotation(Setter.class).name();
        } else {
            setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        try {
            //LOG.debug("Try find method " + setterName + " in " + field.getDeclaringClass().getName());
            Method setter = field.getDeclaringClass().getDeclaredMethod(setterName, field.getType());
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle setterHandle = lookup.unreflect(setter);

            this.setter = (BiConsumer) LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    setterHandle,
                    setterHandle.type()
            ).getTarget().invokeExact();

            //LOG.debug("Resolved setter " + setterName + " for " + field.getDeclaringClass().getName() + " " + field.getName());
        } catch (Throwable ex) {
            //LOG.error("Method " + setterName + " not found in " + field.getDeclaringClass().getName() + " use right access to field");
        }
    }
}
