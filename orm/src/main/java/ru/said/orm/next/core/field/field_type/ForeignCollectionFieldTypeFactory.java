package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.ForeignCollectionField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import static ru.said.orm.next.core.field.field_type.FieldTypeUtils.findFieldByName;
import static ru.said.orm.next.core.field.field_type.FieldTypeUtils.findFieldByType;
import static ru.said.orm.next.core.field.field_type.FieldTypeUtils.getCollectionGenericClass;

/**
 * Created by said on 28.01.2018.
 */
public class ForeignCollectionFieldTypeFactory implements FieldTypeFactory {

    @Override
    public IDBFieldType createFieldType(Field field) throws Exception {
        if (!field.isAnnotationPresent(ForeignCollectionField.class)) {
            return null;
        }
        ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
        ForeignCollectionFieldType fieldType = new ForeignCollectionFieldType();
        String foreignFieldName = foreignCollectionField.foreignFieldName();
        Class<?> foreignFieldClazz = getCollectionGenericClass(field);

        fieldType.setField(field);
        fieldType.setForeignFieldClass(foreignFieldClazz);

        if (foreignFieldName.isEmpty()) {
            fieldType.setForeignField(
                    findFieldByType(
                            field.getDeclaringClass(),
                            fieldType.getForeignFieldClass()
                    ).orElseThrow(() -> new NoSuchFieldException("Foreign field is not defined"))
            );
        } else {
            fieldType.setForeignField(findFieldByName(foreignFieldName, foreignFieldClazz));
        }

        return fieldType;
    }




}