package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.dao.visitor.EntityElement;
import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by said on 14.01.2018.
 */
public interface IDatabaseColumnType extends EntityElement {

    default Object getDefaultValue() {
        return null;
    }

    default boolean isId() {
        return false;
    }

    default boolean isNotNull() {
        return false;
    }

    default boolean isGenerated() {
        return false;
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

    void assign(Object object, Object value);

    Field getField();

    default String getFieldName() {
        return getField().getName();
    }

    default String getFormat() {
        throw new UnsupportedOperationException();
    }

    default int getLength() {
        return 0;
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
