package ru.said.miami.orm.core.cache.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by said on 18.03.17.
 */
public class ReflectionUtils {

    private ReflectionUtils(){ }

    public static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type [] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }
}
