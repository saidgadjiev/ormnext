package utils;

import field.DBField;

import java.lang.reflect.Method;

/**
 * Created by said on 18.03.17.
 */
public class ReflectionUtils {

    private ReflectionUtils(){ }

    public static <T, P> Method getDeclaredMethod(Class<T> clazz, String name, Class<P> type) throws NoSuchMethodException {

        return type == null ? clazz.getDeclaredMethod(name) : clazz.getDeclaredMethod(name, type);
    }

    public static<T, P> void invokeMethod(Class<T> clazz, String name, Class<P> type, Object result, Object value) throws Exception {
        Method method = ReflectionUtils.getDeclaredMethod(clazz, name, type);

        method.invoke(result, value);
    }
}
