package ru.said.miami.orm.core.cache.core.field;

import sun.reflect.CallerSensitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by said on 17.06.17.
 */
public class MethodWrapper {

    private String name;
    private Method method;

    public MethodWrapper(Method method) {
        this.method = method;
    }

    public String getName() {
        if (name == null) {
            name = method.getName();
        }

        return name;
    }

    @CallerSensitive
    public Object invoke(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return method.invoke(obj, args);
    }
}
