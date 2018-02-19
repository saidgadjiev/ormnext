package ru.saidgadjiev.orm.next.core.table;

import ru.saidgadjiev.up.cache.core.Cache;
import ru.saidgadjiev.up.cache.core.CacheBuilder;

/**
 * Created by said on 19.02.2018.
 */
public class TableInfoManager {

    private static final Cache<Class<?>, TableInfo<?>> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    private TableInfoManager() {

    }

    public static<T> TableInfo<T> buildOrGet(Class<?> tClass) throws Exception {
        if (CACHE.contains(tClass)) {
            return (TableInfo<T>) CACHE.get(tClass);
        }

        TableInfo<?> tableInfo = TableInfo.build(tClass);

        CACHE.put(tClass, tableInfo);

        return (TableInfo<T>) tableInfo;
    }
}
