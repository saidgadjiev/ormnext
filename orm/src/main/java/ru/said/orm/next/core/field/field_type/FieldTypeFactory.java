package ru.said.orm.next.core.field.field_type;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public interface FieldTypeFactory {

    IDBFieldType createFieldType(Field field) throws Exception;
}
