package ru.said.miami.orm.core.table;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;

public class TableInfoCache {

    private static final Cache<Class<?>, TableInfo<?>> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    public static<T> TableInfo<T> build(Class<T> clazz) throws NoSuchMethodException, NoSuchFieldException {
        if (CACHE.contains(clazz)) {
            return (TableInfo<T>) CACHE.get(clazz);
        }
        TableInfo<?> tableInfo = TableInfo.build(clazz);

        CACHE.put(clazz, tableInfo);

        return (TableInfo<T>) tableInfo;
    }
}
