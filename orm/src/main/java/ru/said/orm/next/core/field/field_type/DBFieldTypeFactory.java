package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.ForeignCollectionField;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Created by said on 21.01.2018.
 */
public class DBFieldTypeFactory {

    private DBFieldTypeFactory() {

    }

    public static Optional<IDBFieldType> create(Field field) throws Exception {
        if (field.isAnnotationPresent(DBField.class)) {
            DBField dbField = field.getAnnotation(DBField.class);
            if (dbField.foreign()) {
                return Optional.of(ForeignFieldType.ForeignFieldTypeCache.build(field));
            } else {
                return Optional.of(DBFieldType.DBFieldTypeCache.build(field));
            }
        } else if (field.isAnnotationPresent(ForeignCollectionField.class)) {
            return Optional.of(ForeignCollectionFieldType.ForeignCollectionFieldTypeCache.build(field));
        }

        return Optional.empty();
    }
}
