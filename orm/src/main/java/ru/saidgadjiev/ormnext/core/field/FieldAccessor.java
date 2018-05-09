package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.exception.FieldAccessException;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FieldAccessor {

    private static final Log LOG = LoggerFactory.getLogger(FieldAccessor.class);

    private BiConsumer setter;

    private Function getter;

    public FieldAccessor(Field field) {
        if (!resolveGetter(field)) {
            resolveFieldGetter(field);
        }
        if (!resoveSetter(field)) {
            resolveFieldSetter(field);
        }
    }

    public void assign(Object object, Object value) {
        assignBySetter(object, value);
    }

    private void assignBySetter(Object object, Object value) {
        setter.accept(object, value);
    }

    public Object access(Object object) {
        return accessByGetter(object);
    }

    private Object accessByGetter(Object object) {
        return getter.apply(object);
    }

    private boolean resolveGetter(Field field) {
        String getterName;

        if (field.isAnnotationPresent(Getter.class)) {
            getterName = field.getAnnotation(Getter.class).name();
        } else {
            getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        LOG.debug("Try find method %s in %s", getterName, field.getDeclaringClass().getName());

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

            LOG.debug("Resolved getter %s for %s %s", getterName, field.getDeclaringClass().getName(), field.getName());
            return true;
        } catch (Throwable ex) {
            LOG.error("Method %s not found in %s use right access to field", getterName, field.getDeclaringClass().getName());
            return false;
        }
    }

    private boolean resoveSetter(Field field) {
        String setterName;

        if (field.isAnnotationPresent(Setter.class)) {
            setterName = field.getAnnotation(Setter.class).name();
        } else {
            setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        try {
            LOG.debug("Try find method %s in %s", setterName, field.getDeclaringClass().getName());
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

            LOG.debug("Resolved setter %s for %s %s", setterName, field.getDeclaringClass().getName(), field.getName());
            return true;
        } catch (Throwable ex) {
            LOG.error("Method %s not found in %s use right access to field", setterName, field.getDeclaringClass().getName());
            return false;
        }
    }

    private void resolveFieldSetter(Field field) {
        try {
            LOG.debug("Try field direct assign %s %s", field.getDeclaringClass().getName(), field.getName());

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle fieldSetterHandle = lookup.unreflectSetter(field);

            this.setter = (BiConsumer) LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    fieldSetterHandle,
                    fieldSetterHandle.type()
            ).getTarget().invokeExact();
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FieldAccessException(ex);
        }
    }

    private void resolveFieldGetter(Field field) {
        try {
            LOG.debug("Try field direct access %s %s", field.getDeclaringClass().getName(), field.getName());

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle fieldGetterHandle = lookup.unreflectGetter(field);

            this.getter = (Function) LambdaMetafactory.metafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    fieldGetterHandle,
                    fieldGetterHandle.type()
            ).getTarget().invokeExact();
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FieldAccessException(ex);
        }
    }
}
