package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.field.CollectionType;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.ForeignCollectionField;
import ru.saidgadjiev.orm.next.core.utils.DatabaseMetaDataUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static ru.saidgadjiev.orm.next.core.field.fieldtype.FieldTypeUtils.*;

/**
 * Created by said on 28.01.2018.
 */
public class ForeignCollectionColumnTypeFactory implements ColumnTypeFactory {

    @Override
    public IDatabaseColumnType createFieldType(Field field) {
        if (!field.isAnnotationPresent(ForeignCollectionField.class)) {
            return null;
        }
        ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
        ForeignCollectionColumnType fieldType = new ForeignCollectionColumnType();
        String foreignFieldName = foreignCollectionField.foreignFieldName();
        Class<?> foreignFieldClazz = getCollectionGenericClass(field);

        fieldType.setFetchType(foreignCollectionField.fetchType());
        fieldType.setCollectionType(resolveCollectionType(field.getType()));
        fieldType.setField(field);
        fieldType.setForeignFieldClass(foreignFieldClazz);
        try {
            fieldType.setFieldAccessor(new FieldAccessor(field));
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }

        Field foreignField;

        if (foreignFieldName.isEmpty()) {
            foreignField = findFieldByType(
                    field.getDeclaringClass(),
                    fieldType.getForeignFieldClass()
            ).get();
        } else {
           foreignField = findFieldByName(foreignFieldName, foreignFieldClazz);
        }

        fieldType.setForeignField(foreignField);
        fieldType.setForeignColumnName(FieldTypeUtils.resolveForeignColumnTypeName(foreignField));
        fieldType.setForeignTableName(DatabaseMetaDataUtils.resolveTableName(foreignField.getDeclaringClass()));

        return fieldType;
    }

    private CollectionType resolveCollectionType(Class<?> collectionClass) {
        if (collectionClass.equals(List.class)) {
            return CollectionType.LIST;
        }

        if (collectionClass.equals(Set.class)) {
            return CollectionType.SET;
        }

        return CollectionType.UNDEFINED;
    }

}
