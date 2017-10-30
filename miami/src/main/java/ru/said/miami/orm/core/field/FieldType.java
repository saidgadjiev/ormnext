package ru.said.miami.orm.core.field;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldType {

    private DBFieldType dbFieldType;

    private ForeignCollectionFieldType foreignCollectionFieldType;

    public static Optional<FieldType> buildFieldType(Field field) throws NoSuchMethodException, NoSuchFieldException {
        if (!field.isAnnotationPresent(DBField.class) && !field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.empty();
        }
        FieldType fieldType = new FieldType();

        if (field.isAnnotationPresent(DBField.class)) {
            fieldType.dbFieldType = DBFieldType.DBFieldTypeCache.build(field);
        } else if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            fieldType.foreignCollectionFieldType = ForeignCollectionFieldType.ForeignCollectionFieldTypeCache.build(field);
        }

        return Optional.of(fieldType);
    }

    public boolean isDBFieldType() {
        return dbFieldType != null;
    }

    public boolean isForeignCollectionFieldType() {
        return foreignCollectionFieldType != null;
    }

    public DBFieldType getDbFieldType() {
        return dbFieldType;
    }

    public ForeignCollectionFieldType getForeignCollectionFieldType() {
        return foreignCollectionFieldType;
    }
}
