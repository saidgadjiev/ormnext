package ru.saidgadjiev.proxymaker.bytecode.common;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteCodeUtils {

    private ByteCodeUtils() {

    }

    public static byte[] toByteArray(List<Byte> list) {
        byte[] array = new byte[list.size()];
        AtomicInteger index = new AtomicInteger();

        list.forEach(aByte -> array[index.getAndIncrement()] = aByte);

        return array;
    }

    public static String toJvmName(String classname) {
        return classname.replace('.', '/');
    }

    public static int dataSize(String desc) {
        return dataSize(desc, true);
    }

    private static int dataSize(String desc, boolean withRet) {
        int n = 0;
        char c = desc.charAt(0);
        if (c == '(') {
            int i = 1;
            for (; ; ) {
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
                    if (i <= 0)
                        throw new IndexOutOfBoundsException("bad descriptor");
                } else
                    ++i;

                if (!array && (c == 'J' || c == 'D'))
                    n -= 2;
                else
                    --n;
            }
        }

        if (withRet)
            if (c == 'J' || c == 'D')
                n += 2;
            else if (c != 'V')
                ++n;

        return n;
    }

    public static String makeDescriptor(Method m) {
        Class[] params = m.getParameterTypes();
        return makeDescriptor(params, m.getReturnType());
    }

    public static String makeDescriptor(Class[] params, Class retType) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append('(');
        for (int i = 0; i < params.length; i++)
            makeDesc(sbuf, params[i]);

        sbuf.append(')');
        if (retType != null)
            makeDesc(sbuf, retType);

        return sbuf.toString();
    }

    private static String makeDesc(StringBuilder sbuf, Class type) {
        if (type.isArray()) {
            sbuf.append('[');
            makeDesc(sbuf, type.getComponentType());
        } else if (type.isPrimitive()) {
            if (type == Void.TYPE)
                sbuf.append('V');
            else if (type == Integer.TYPE)
                sbuf.append('I');
            else if (type == Byte.TYPE)
                sbuf.append('B');
            else if (type == Long.TYPE)
                sbuf.append('J');
            else if (type == Double.TYPE)
                sbuf.append('D');
            else if (type == Float.TYPE)
                sbuf.append('F');
            else if (type == Character.TYPE)
                sbuf.append('C');
            else if (type == Short.TYPE)
                sbuf.append('S');
            else if (type == Boolean.TYPE)
                sbuf.append('Z');
            else
                throw new RuntimeException("bad type: " + type.getName());
        } else
            sbuf.append('L').append(type.getName().replace('.', '/'))
                    .append(';');

        return sbuf.toString();
    }
}
