package ru.saidgadjiev.proxymaker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static ru.saidgadjiev.proxymaker.SecurityHelper.getDeclaredMethod;
import static ru.saidgadjiev.proxymaker.SecurityHelper.setAccessible;

/**
 * A helper class for {@link ProxyMaker}.
 *
 * @see ProxyMaker
 */
public final class ProxyFactoryHelper {

    /**
     * Method {@link ClassLoader#defineClass} for create new class {@link Class} from bytecode.
     */
    private static java.lang.reflect.Method defineClass;

    /**
     * This array represent primitive type class {@link Class}.
     */
    static final Class[] PRIMITIVE_TYPES = {
            Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE,
            Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE
    };

    /**
     * This array represent primitive wrapper types fully qualified class names.
     */
    static final String[] WRAPPER_TYPES = {
            "java.lang.Boolean", "java.lang.Byte", "java.lang.Character",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long",
            "java.lang.Float", "java.lang.Double", "java.lang.Void"
    };

    /**
     * The descriptors of the constructors of wrapper classes.
     */
    static final String[] WRAPPER_DESC = {
            "(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V",
            "(F)V", "(D)V"
    };

    /**
     * The data size of primitive types.  {@code long}
     * and @{code double} are 2; the others are 1.
     */
    static final int[] DATA_SIZE = {
            1, 1, 1, 1, 1, 2, 1, 2
    };

    /**
     * The names of methods for obtaining a primitive value
     * from a wrapper object.  For example, {@code intValue()}
     * is such a method for obtaining an integer value from a
     * {@link java.lang.Integer} object.
     */
    static final String[] UNWARP_METHODS = {
            "booleanValue", "byteValue", "charValue", "shortValue",
            "intValue", "longValue", "floatValue", "doubleValue"
    };

    /**
     * The descriptors of the unwrapping methods contained
     * in {@code UNWARP_METHODS}.
     */
    static final String[] UNWRAP_DESC = {
            "()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D"
    };

    /**
     * Primitive type names. Use for resolve primitive class by primitive type name
     * in {@link ProxyRuntimeHelper#findMethod(String, String, String[])}
     *
     * @see ProxyRuntimeHelper
     */
    private static final String[] PRIMITIVE_TYPE_NAMES = {
            "boolean", "byte", "char", "short", "int", "long", "float", "double"
    };

    static {
        try {
            Class<?> cl = Class.forName("java.lang.ClassLoader");

            defineClass = getDeclaredMethod(
                    cl, "defineClass", new Class[]{String.class, byte[].class, int.class, int.class}
            );
        } catch (Exception e) {
            throw new RuntimeException("cannot initialize", e);
        }
    }

    /**
     * Util class cant't be created.
     */
    private ProxyFactoryHelper() {
    }

    /**
     * Return primitive type index from primitive type array by primitive type class {@link Class}.
     * It will be use for associate this index in another info arrays {@link ProxyFactoryHelper#UNWARP_METHODS} etc.
     *
     * @param type target type class
     * @return type index in {@link ProxyFactoryHelper#PRIMITIVE_TYPES}
     */
    public static int typeIndex(Class type) {
        Class[] list = PRIMITIVE_TYPES;
        int n = list.length;

        for (int i = 0; i < n; i++) {
            if (list[i] == type) {
                return i;
            }
        }

        throw new RuntimeException("bad type:" + type.getName());
    }

    /**
     * Return primitive type index by type name from primitive type array.
     * It will be use for associate this index in another info arrays {@link ProxyFactoryHelper#UNWARP_METHODS} etc.
     *
     * @param typeName target type name
     * @return type index from {@link ProxyFactoryHelper#PRIMITIVE_TYPE_NAMES}
     * @see ProxyRuntimeHelper#findMethod
     */
    public static int typeIndex(String typeName) {
        int n = PRIMITIVE_TYPE_NAMES.length;

        for (int i = 0; i < n; i++) {
            if (PRIMITIVE_TYPE_NAMES[i].equals(typeName)) {
                return i;
            }
        }

        throw new RuntimeException("bad type:" + typeName);
    }

    /**
     * Check type is primitive by type name.
     *
     * @param typeName target type name
     * @return true if primitive else false
     * @see ProxyRuntimeHelper#findMethod
     */
    public static boolean isPrimitive(String typeName) {
        for (String primitiveTypeName : PRIMITIVE_TYPE_NAMES) {
            if (primitiveTypeName.equals(typeName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Create a new class {@link Class} from requested classFile {@link ClassFile}.
     *
     * @param classFile target class file {@link ClassFile}
     * @param cl        classloader
     * @return new class {@link Class}
     */
    public static Class<?> toClass(ClassFile classFile, ClassLoader cl) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream data = new DataOutputStream(outputStream)) {
            classFile.write(data);
            byte[] byteArray = outputStream.toByteArray();

            setAccessible(defineClass, true);

            Class<?> proxyClass = (Class<?>) defineClass.invoke(
                    cl, classFile.getClassName(), byteArray, 0, byteArray.length
            );

            setAccessible(defineClass, false);

            return proxyClass;
        } catch (Exception ex) {
            throw new InternalError(ex);
        }
    }
}
