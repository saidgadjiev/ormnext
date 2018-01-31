package ru.said.orm.next.core.table.utils;

import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.field_type.DBFieldTypeFactory;
import ru.said.orm.next.core.field.field_type.IDBFieldType;
import ru.said.orm.next.core.table.DBTable;

import java.lang.reflect.Field;
import java.util.Optional;

public class TableInfoUtils {

    private TableInfoUtils() {}

    public static Optional<IDBFieldType> resolvePrimaryKey(Class<?> foreignFieldClass) throws Exception {
        for (Field field : foreignFieldClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class) && field.getAnnotation(DBField.class).id()) {
                return Optional.of(new DBFieldTypeFactory().createFieldType(field));
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
