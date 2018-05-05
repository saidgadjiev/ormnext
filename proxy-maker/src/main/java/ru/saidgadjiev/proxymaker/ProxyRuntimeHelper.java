package ru.saidgadjiev.proxymaker;

import java.lang.reflect.Method;

public class ProxyRuntimeHelper {

    private ProxyRuntimeHelper() {}

    public static Method findMethod(String className, String name, String[] parametrTypeClassNames) {
        try {
            Class[] parametrTypes = new Class[parametrTypeClassNames.length];
            int i = 0;

            for (String parametrTypeClassName: parametrTypeClassNames) {
                if (ProxyFactoryHelper.isPrimitive(parametrTypeClassName)) {
                    parametrTypes[i++] = ProxyFactoryHelper.PRIMITIVE_TYPES[ProxyFactoryHelper.typeIndex(parametrTypeClassName)];
                } else {
                    parametrTypes[i++] = Class.forName(parametrTypeClassName);
                }
            }
            return Class.forName(className).getMethod(name, parametrTypes);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}
