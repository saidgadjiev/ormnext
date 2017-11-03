package ru.said.miami.orm.core.field;

import java.lang.reflect.Field;
import java.util.Optional;


//TODO: Надо свести все филды к общему интерфейсу. Возможно их стоит сделать наследниками DBFieldType
//TODO: Вынести в фабрики
//TODO: объединить все типы в один интерфейс
public class FieldType {

    private DBFieldType dbFieldType;

    private ForeignCollectionFieldType foreignCollectionFieldType;

    private ForeignFieldType foreignFieldType;

    public static Optional<FieldType> buildFieldType(Field field) throws NoSuchMethodException, NoSuchFieldException {
        if (!field.isAnnotationPresent(DBField.class) && !field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.empty();
        }
        FieldType fieldType = new FieldType();

        if (field.isAnnotationPresent(DBField.class)) {
            DBField dbField = field.getAnnotation(DBField.class);
            DBFieldType dbFieldType = DBFieldType.build(field);

            if (dbField.foreign()) {
                fieldType.foreignFieldType = ForeignFieldType.build(field);
            } else {
                fieldType.dbFieldType = DBFieldType.DBFieldTypeCache.build(field);
            }
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

    public boolean getForeignFieldType() {
        return foreignFieldType != null;
    }

    public ForeignFieldType getForeignField() {
        return foreignFieldType;
    }
}
