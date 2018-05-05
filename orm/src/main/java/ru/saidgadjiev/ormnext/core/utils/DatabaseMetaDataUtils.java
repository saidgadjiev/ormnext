package ru.saidgadjiev.ormnext.core.utils;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseMetaDataUtils {

    private final static Map<Class<?>, String> resolvedTableNames = new HashMap<>();

    private DatabaseMetaDataUtils() {}

    public static Optional<IDatabaseColumnType> resolvePrimaryKey(Class<?> foreignFieldClass) {
        for (Field field : foreignFieldClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseColumn.class) && field.getAnnotation(DatabaseColumn.class).id()) {
                return Optional.ofNullable(DatabaseColumnType.build(field));
            }
        }

        return Optional.empty();
    }

    public static String resolveTableName(Class<?> clazz) {
        if (resolvedTableNames.containsKey(clazz)) {
            return resolvedTableNames.get(clazz);
        }
        String tableName = "";

        if (clazz.isAnnotationPresent(DatabaseEntity.class)) {
            DatabaseEntity databaseEntity = clazz.getAnnotation(DatabaseEntity.class);

            tableName = databaseEntity.name();
        }

        tableName = tableName.isEmpty() ? clazz.getSimpleName().toLowerCase() : tableName;
        resolvedTableNames.put(clazz, tableName);

        return tableName;
    }

}
