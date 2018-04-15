package ru.saidgadjiev.orm.next.core.table;

import ru.saidgadjiev.up.cache.core.Cache;
import ru.saidgadjiev.up.cache.core.CacheBuilder;

/**
 * Created by said on 19.02.2018.
 */
public class TableInfoManager {

    private static final Cache<Class<?>, DatabaseEntityMetadata<?>> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    private TableInfoManager() {

    }

    public static<T> DatabaseEntityMetadata<T> buildOrGet(Class<T> tClass) {
        if (CACHE.contains(tClass)) {
            return (DatabaseEntityMetadata<T>) CACHE.get(tClass);
        }

        DatabaseEntityMetadata<?> databaseEntityMetadata = DatabaseEntityMetadata.build(tClass);

        CACHE.put(tClass, databaseEntityMetadata);

        return (DatabaseEntityMetadata<T>) databaseEntityMetadata;
    }
}
