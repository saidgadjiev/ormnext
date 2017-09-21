package ru.said.miami.orm.core.table;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.FieldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TableInfo {

    private final List<FieldType> fieldTypes;
    private String tableName;

    private TableInfo(String tableName, List<FieldType> fieldTypes) {
        this.tableName = tableName;
        this.fieldTypes = fieldTypes;
    }

    public String getTableName() {
        return tableName;
    }

    public List<FieldType> getFieldTypes() {
        return Collections.unmodifiableList(fieldTypes);
    }

    public static<T> TableInfo buildTableInfo(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(DBTable.class)) {
            return null;
        }
        List<FieldType> fieldTypes = new ArrayList<>();

        for (Field field: clazz.getDeclaredFields()) {
            FieldType fieldType = FieldType.buildFieldType(field);

            if (fieldType != null) {
                fieldTypes.add(fieldType);
            }
        }
        if (fieldTypes.isEmpty()) {
            throw new IllegalArgumentException("No fields have a " + DBField.class.getSimpleName()
                    + " annotation in " + clazz);
        }

        return new TableInfo(clazz.getAnnotation(DBTable.class).name(), fieldTypes);
    }
}
