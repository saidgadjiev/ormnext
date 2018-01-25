package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.persisters.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 14.01.2018.
 */
public interface IDBFieldType {

    boolean isId();

    boolean isNotNull();

    boolean isGenerated();

    String getColumnName();

    DataType getDataType();

    Object access(Object object) throws InvocationTargetException, IllegalAccessException;

    DataPersister getDataPersister();

    void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException;

    Field getField();

    int getLength();

    default boolean isDbFieldType() {
        return false;
    }

    default boolean isForeignFieldType() {
        return false;
    }

    default boolean isForeignCollectionFieldType() {
        return false;
    }
}
