package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.persister.BaseDataPersister;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityElement;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityElement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by said on 14.01.2018.
 */
public interface IDatabaseColumnType extends EntityElement {

    default String getDefaultDefinition() {
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

    Object access(Object object) throws SQLException;

    DataPersister getDataPersister();

    default void assignId(Object object, Number value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    void assign(Object object, Object value) throws SQLException;

    Field getField();

    default String getFieldName() {
        return getField().getName();
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

    default Class<?> getOwnerClass() {
        return getField().getDeclaringClass();
    }

    String getOwnerTableName();

    void setOwnerTableName(String ownerTableName);

    default Optional<Converter<?, ?>> getConverter() {
        return Optional.empty();
    }

}
