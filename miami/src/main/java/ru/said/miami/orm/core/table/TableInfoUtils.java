package ru.said.miami.orm.core.table;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DBFieldType;

import java.lang.reflect.Field;
import java.util.Optional;

public class TableInfoUtils {

    private TableInfoUtils() {}

    public static Optional<DBFieldType> resolvePrimaryKey(Class<?> foreignFieldClass) throws NoSuchMethodException, NoSuchFieldException {
        for (Field field : foreignFieldClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).id()) {
                return Optional.of(DBFieldType.DBFieldTypeCache.build(field));
            }
        }

        return Optional.empty();
    }

    public static String resolveTableName(Class<?> clazz) {
        String tableName = "";

        if (clazz.isAnnotationPresent(DBTable.class)) {
            DBTable dbTable = clazz.getAnnotation(DBTable.class);

            tableName = dbTable.name();
        }

        return tableName.isEmpty() ? clazz.getSimpleName().toLowerCase() : tableName;
    }

}
