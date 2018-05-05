package ru.saidgadjiev.proxymaker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static ru.saidgadjiev.proxymaker.SecurityHelper.getDeclaredMethod;
import static ru.saidgadjiev.proxymaker.SecurityHelper.setAccessible;

public class ProxyFactoryHelper {

    private static java.lang.reflect.Method defineClass;

    public static final Class[] PRIMITIVE_TYPES = {
            Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE,
            Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE
    };

    public static final String[] WRAPPER_TYPES = {
            "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long",
            "java.lang.Float", "java.lang.Double", "java.lang.Void"
    };

    public static final String[] WRAPPER_DESC = {
            "(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V",
            "(F)V", "(D)V"
    };

    public static final int[] DATA_SIZE = {
            1, 1, 1, 1, 1, 2, 1, 2
    };

    public static final String[] UNWARP_METHODS = {
            "booleanValue", "byteValue", "charValue", "shortValue",
            "intValue", "longValue", "floatValue", "doubleValue"
    };

    public static final String[] UNWRAP_DESC = {
            "()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D"
    };

    public static final String[] PRIMITIVE_TYPE_NAMES = {
            "boolean", "byte", "char", "short", "int", "long", "float", "double"
    };

    static {
        try {
            Class<?> cl = Class.forName("java.lang.ClassLoader");

            defineClass = getDeclaredMethod(
                    cl,
                    "defineClass",
                    new Class[] { String.class, byte[].class,
                            int.class, int.class });
        }
        catch (Exception e) {
            throw new RuntimeException("cannot initialize");
        }
    }

    private ProxyFactoryHelper() {

    }

    public static final int typeIndex(Class type) {
        Class[] list = PRIMITIVE_TYPES;
        int n = list.length;
        for (int i = 0; i < n; i++)
            if (list[i] == type)
                return i;

        throw new RuntimeException("bad type:" + type.getName());
    }

    public static final int typeIndex(String typeName) {
        int n = PRIMITIVE_TYPE_NAMES.length;

        for (int i = 0; i < n; i++)
            if (PRIMITIVE_TYPE_NAMES[i].equals(typeName))
                return i;

        throw new RuntimeException("bad type:" + typeName);
    }

    public static final boolean isPrimitive(String typeName) {
        int n = PRIMITIVE_TYPE_NAMES.length;

        for (int i = 0; i < n; i++)
            if (PRIMITIVE_TYPE_NAMES[i].equals(typeName))
                return true;

        return false;
    }

    public static Class<?> toClass(ClassFile classFile) throws IOException, InvocationTargetException, IllegalAccessException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); DataOutputStream data = new DataOutputStream(outputStream)) {
            classFile.write(data);
            byte[] byteArray = outputStream.toByteArray();

            FileOutputStream fileOutputStream = new FileOutputStream("C:/test_proxy_maker.class");

            fileOutputStream.write(byteArray);
            setAccessible(defineClass, true);

            Class<?> proxyClass = (Class<?>) defineClass.invoke(ClassLoader.getSystemClassLoader(), classFile.getClassName(), byteArray, 0, byteArray.length);

            setAccessible(defineClass, false);

            return proxyClass;
        }
    }
}
