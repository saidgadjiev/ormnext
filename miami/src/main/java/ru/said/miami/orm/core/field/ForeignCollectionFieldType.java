package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.TableInfo;

import java.lang.reflect.Field;
import java.util.Collection;

public class ForeignCollectionFieldType {

    private Field field;

    private String foreignFieldName;

    private TableInfo<?> foreignTableInfo;

    public Field getField() {
        return field;
    }

    public String getForeignFieldName() {
        return foreignFieldName;
    }

    public TableInfo<?> getForeignTableInfo() {
        return foreignTableInfo;
    }

    public void add(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            ((Collection) field.get(object)).add(value);
            field.setAccessible(false);
        }
        ((Collection) field.get(object)).add(value);
    }

    public static ForeignCollectionFieldType build(Field field) {
        ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
        ForeignCollectionFieldType fieldType = new ForeignCollectionFieldType();

        fieldType.field = field;
        fied
        fieldType.foreignFieldName = foreignCollectionField.foreignFieldName();

        return fieldType;
    }
}
