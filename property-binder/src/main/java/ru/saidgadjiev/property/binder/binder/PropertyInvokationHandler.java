package ru.saidgadjiev.property.binder.binder;


import ru.saidgadjiev.property.binder.annotations.Property;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;


public class PropertyInvokationHandler implements InvocationHandler {
    private Properties properties;

    PropertyInvokationHandler(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String propertyName = method.getAnnotation(Property.class).name();

        if (method.getReturnType().equals(URL.class)) {
            return new URL(properties.getProperty(propertyName));
        }
        if (method.getReturnType().equals(boolean.class)) {
            return properties.getProperty(propertyName).equals("true");
        }

        return properties.get(propertyName);
    }
}
