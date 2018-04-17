package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.dao.visitor.EntityElement;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 14.01.2018.
 */
public interface IDatabaseColumnType extends EntityElement {

    default Object getDefaultValue() {
        throw new UnsupportedOperationException();
    }

    default boolean isId() {
        throw new UnsupportedOperationException();
    }

    default boolean isNotNull() {
        throw new UnsupportedOperationException();
    }

    default boolean isGenerated() {
        throw new UnsupportedOperationException();
    }

    default String getColumnName() {
        throw new UnsupportedOperationException();
    }

    default int getDataType() {
        throw new UnsupportedOperationException();
    }

    Object access(Object object) throws InvocationTargetException, IllegalAccessException;

    DataPersister getDataPersister();

    default void assignId(Object object, Number value) throws IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException();
    }

    void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException;

    Field getField();

    default String getFieldName() {
        return getField().getName();
    }

    default String getFormat() {
        throw new UnsupportedOperationException();
    }

    default int getLength() {
        throw new UnsupportedOperationException();
    }

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
