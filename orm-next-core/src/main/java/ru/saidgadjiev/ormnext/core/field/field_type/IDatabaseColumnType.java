package ru.saidgadjiev.ormnext.core.field.field_type;

import ru.saidgadjiev.ormnext.core.field.data_persister.ColumnConverter;
import ru.saidgadjiev.ormnext.core.field.data_persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityElement;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * This interface represent database column type.
 */
public interface IDatabaseColumnType extends EntityElement {

    /**
     * Return default column definition. It will be append in create type eg. 'DEFAULT defaultDefinition'.
     *
     * @return current default definition
     */
    String getDefaultDefinition();

    /**
     * True if column is id.
     *
     * @return true if column is id
     */
    boolean isId();

    /**
     * True if column is not null.
     *
     * @return true if column is not null
     */
    boolean isNotNull();

    /**
     * True if column is generated.
     *
     * @return true if column is generated
     */
    boolean isGenerated();

    /**
     * Return column name.
     *
     * @return column name
     */
    String getColumnName();

    /**
     * Return column type.
     *
     * @return Return column type
     */
    int getDataType();

    /**
     * Access to field by object and return field value.
     *
     * @param object target object
     * @return field value
     */
    Object access(Object object);

    /**
     * Return data persister associated with this column.
     *
     * @return data persister
     * @see DataPersister
     */
    DataPersister getDataPersister();

    /**
     * Assign field to object with value.
     *
     * @param object target object
     * @param value  target value
     */
    void assign(Object object, Object value);

    /**
     * Return field.
     *
     * @return field
     */
    Field getField();

    /**
     * Column length.
     *
     * @return column length
     */
    int getLength();

    /**
     * Column owner table name.
     *
     * @return owner table name
     */
    String getTableName();

    /**
     * Return field converters. ColumnConverter use for convert field value to sql value and sql to java value.
     *
     * @return current field converters
     * @see ColumnConverter
     */
    Optional<List<ColumnConverter<?, Object>>> getColumnConverters();

    /**
     * False for ignore in insert.
     *
     * @return insertable
     */
    boolean insertable();

    /**
     * False for ignore in update.
     *
     * @return updatable
     */
    boolean updatable();

    /**
     * True if this instance {@link DatabaseColumnType}.
     *
     * @return true if this instance {@link DatabaseColumnType}
     */
    boolean isDbColumnType();

    /**
     * True if this instance {@link ForeignColumnType}.
     *
     * @return true if this instance {@link ForeignColumnType}
     */
    boolean isForeignColumnType();

    /**
     * True if this instance {@link ForeignCollectionColumnType}.
     *
     * @return true if this instance {@link ForeignCollectionColumnType}
     */
    boolean isForeignCollectionColumnType();
}
