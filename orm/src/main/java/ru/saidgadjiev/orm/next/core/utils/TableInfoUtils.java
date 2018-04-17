package ru.saidgadjiev.orm.next.core.utils;

import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.fieldtype.DatabaseColumnTypeFactory;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntity;

import java.lang.reflect.Field;
import java.util.Optional;

public class TableInfoUtils {

    private TableInfoUtils() {}

    public static Optional<IDatabaseColumnType> resolvePrimaryKey(Class<?> foreignFieldClass) {
        for (Field field : foreignFieldClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseColumn.class) && field.getAnnotation(DatabaseColumn.class).id()) {
                return Optional.of(new DatabaseColumnTypeFactory().createFieldType(field));
            }
        }

        return Optional.empty();
    }

    public static String resolveTableName(Class<?> clazz) {
        String tableName = "";

        if (clazz.isAnnotationPresent(DatabaseEntity.class)) {
            DatabaseEntity databaseEntity = clazz.getAnnotation(DatabaseEntity.class);

            tableName = databaseEntity.name();
        }

        return tableName.isEmpty() ? clazz.getSimpleName().toLowerCase() : tableName;
    }

}
