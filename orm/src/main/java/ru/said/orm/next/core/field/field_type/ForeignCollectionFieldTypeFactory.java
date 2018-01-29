package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.ForeignCollectionField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

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


    private static Class<?> getCollectionGenericClass(Field field) {
        Type type = field.getGenericType();
        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

        return (Class<?>) genericTypes[0];
    }

    private static Field findFieldByName(String foreignFieldName, Class<?> clazz) throws NoSuchFieldException {
        return clazz.getDeclaredField(foreignFieldName);
    }

    private static Optional<Field> findFieldByType(Class<?> type, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(DBField.class) && field.getType() == type)
                .findFirst();
    }

}
