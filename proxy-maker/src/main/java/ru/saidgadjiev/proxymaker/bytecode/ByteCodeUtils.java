package ru.saidgadjiev.proxymaker.bytecode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utils for bytecode instructions.
 */
public final class ByteCodeUtils {

    /**
     * Utils class instance cant't be created.
     */
    private ByteCodeUtils() { }

    /**
     * Convert {@link List} to byte array.
     *
     * @param list target list
     * @return byte array
     */
    public static byte[] toByteArray(List<Byte> list) {
        byte[] array = new byte[list.size()];
        AtomicInteger index = new AtomicInteger();

        list.forEach(aByte -> array[index.getAndIncrement()] = aByte);

        return array;
    }

    /**
     * Convert classname to jvm represent.
     *
     * @param classname target fully-qualified classname
     * @return jvm classname where . replaced by /
     */
    public static String toJvmName(String classname) {
        return classname.replace('.', '/');
    }

    /**
     * Size of descriptor for bytecode.
     *
     * @param desc target descriptor
     * @return size of desc
     */
    public static int dataSize(String desc) {
        int n = 0;
        char c = desc.charAt(0);
        if (c == '(') {
            int i = 1;

            while (true) {
                c = desc.charAt(i);
                if (c == ')') {
                    c = desc.charAt(i + 1);
                    break;
                }

                boolean array = false;
                while (c == '[') {
                    array = true;
                    c = desc.charAt(++i);
                }

                if (c == 'L') {
                    i = desc.indexOf(';', i) + 1;
                    if (i <= 0) {
                        throw new IndexOutOfBoundsException("bad descriptor");
                    }
                } else {
                    ++i;
                }
                if (!array && (c == 'J' || c == 'D')) {
                    n -= 2;
                } else {
                    --n;
                }
            }
        }
        if (c == 'J' || c == 'D') {
            n += 2;
        } else if (c != 'V') {
            ++n;
        }

        return n;
    }

    /**
     * Make {@link Method} decriptor for bytecode.
     *
     * @param method target method
     * @return method descriptor
     */
    public static String makeDescriptor(Method method) {
        Class[] params = method.getParameterTypes();

        return makeDescriptor(params, method.getReturnType());
    }

    /**
     * Make decriptor for bytecode by {@code params} and {@code retType}.
     *
     * @param params target params
     * @param retType target return type
     * @return method descriptor
     */
    public static String makeDescriptor(Class[] params, Class retType) {
        StringBuilder sbuf = new StringBuilder();

        sbuf.append('(');
        for (Class param : params) {
            makeDesc(sbuf, param);
        }

        sbuf.append(')');
        if (retType != null) {
            makeDesc(sbuf, retType);
        }

        return sbuf.toString();
    }

    /**
     * Make decriptor for bytecode by {@code type}.
     *
     * @param sbuf buffer for write descriptor chars
     * @param type target type
     * @return type descriptor
     */
    private static String makeDesc(StringBuilder sbuf, Class type) {
        if (type.isArray()) {
            sbuf.append('[');
            makeDesc(sbuf, type.getComponentType());
        } else if (type.isPrimitive()) {
            if (type == Void.TYPE) {
                sbuf.append('V');
            } else if (type == Integer.TYPE) {
                sbuf.append('I');
            } else if (type == Byte.TYPE) {
                sbuf.append('B');
            } else if (type == Long.TYPE) {
                sbuf.append('J');
            } else if (type == Double.TYPE) {
                sbuf.append('D');
            } else if (type == Float.TYPE) {
                sbuf.append('F');
            } else if (type == Character.TYPE) {
                sbuf.append('C');
            } else if (type == Short.TYPE) {
                sbuf.append('S');
            } else if (type == Boolean.TYPE) {
                sbuf.append('Z');
            } else {
                throw new RuntimeException("bad type: " + type.getName());
            }
        } else {
            sbuf.append('L').append(type.getName().replace('.', '/'))
                    .append(';');
        }

        return sbuf.toString();
    }
}
