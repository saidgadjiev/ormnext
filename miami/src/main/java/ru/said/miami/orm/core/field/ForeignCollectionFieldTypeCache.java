package ru.said.miami.orm.core.field;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;

import java.lang.reflect.Field;

public class ForeignCollectionFieldTypeCache {

    private static final Cache<Field, ForeignCollectionFieldType> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    public static ForeignCollectionFieldType build(Field field) throws NoSuchMethodException, NoSuchFieldException {
        if (CACHE.contains(field)) {
            return CACHE.get(field);
        }
        ForeignCollectionFieldType fieldType = ForeignCollectionFieldType.build(field);

        CACHE.put(field, fieldType);

        return fieldType;
    }
}
