package utils;

import field.DBField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    public static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type [] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }
}
