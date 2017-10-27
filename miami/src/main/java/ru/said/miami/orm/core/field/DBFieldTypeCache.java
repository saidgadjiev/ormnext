package ru.said.miami.orm.core.field;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;

import java.lang.reflect.Field;

public class DBFieldTypeCache {

    private static final Cache<Field, DBFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    public static DBFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        if (CACHE.contains(field)) {
            return CACHE.get(field);
        }
        DBFieldType dbFieldType = DBFieldType.build(field);

        CACHE.put(field, dbFieldType);

        return dbFieldType;
    }
}
