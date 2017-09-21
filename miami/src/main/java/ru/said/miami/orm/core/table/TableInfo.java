package ru.said.miami.orm.core.table;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.FieldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class TableInfo {

    private final List<FieldType> fieldTypes;

    private final Optional<FieldType> idField;

    private String tableName;

    private TableInfo(String tableName, List<FieldType> fieldTypes) {
        this.tableName = tableName;
        this.fieldTypes = fieldTypes;
        idField = fieldTypes.stream().filter(fieldType -> fieldType.isId() && fieldType.isGenerated()).findFirst();
    }

    public String getTableName() {
        return tableName;
    }

    public List<FieldType> getFieldTypes() {
        return Collections.unmodifiableList(fieldTypes);
    }

    public Optional<FieldType> getIdField() {
        return idField;
    }

    public static<T> TableInfo buildTableInfo(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(DBTable.class)) {
            return null;
        }
        List<FieldType> fieldTypes = new ArrayList<>();

        for (Field field: clazz.getDeclaredFields()) {
            FieldType.buildFieldType(field).ifPresent(fieldTypes::add);
        }
        if (fieldTypes.isEmpty()) {
            throw new IllegalArgumentException("No fields have a " + DBField.class.getSimpleName()
                    + " annotation in " + clazz);
        }

        return new TableInfo(clazz.getAnnotation(DBTable.class).name(), fieldTypes);
    }
}
