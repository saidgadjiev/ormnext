package ru.saidgadjiev.orm.next.core.field.field_type;

import ru.saidgadjiev.orm.next.core.field.persisters.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 14.01.2018.
 */
public interface IDBFieldType {

    default Object getDefaultValue() {
        return null;
    }

    boolean isId();

    boolean isNotNull();

    boolean isGenerated();

    String getColumnName();

    int getDataType();

    Object access(Object object) throws InvocationTargetException, IllegalAccessException;

    DataPersister getDataPersister();

    default void assignId(Object object, Number value) throws IllegalAccessException, InvocationTargetException {

    }

    void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException;

    Field getField();

    default String getFieldName() {
        return getField().getName();
    }

    default String getFormat() {
        return null;
    }

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
