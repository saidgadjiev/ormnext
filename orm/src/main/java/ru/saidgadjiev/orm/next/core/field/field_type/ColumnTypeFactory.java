package ru.saidgadjiev.orm.next.core.field.field_type;

import java.lang.reflect.Field;

/**
 * Created by said on 27.01.2018.
 */
public interface ColumnTypeFactory {

    IDatabaseColumnType createFieldType(Field field) throws Exception;
}
