package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.SqlType;
import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityElement;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * This interface represent database column type.
 *
 * @author Said Gadjiev
 */
public interface DatabaseColumnType extends EntityElement {

    /**
     * Return default column definition. It will be append in create type eg. 'DEFAULT defaultDefinition'.
     *
     * @return current default definition
     */
    String defaultDefinition();

    /**
     * True if column is id.
     *
     * @return true if column is id
     */
    boolean id();

    /**
     * True if column is not null.
     *
     * @return true if column is not null
     */
    boolean notNull();

    /**
     * True if column is generated.
     *
     * @return true if column is generated
     */
    boolean generated();

    /**
     * Return column name.
     *
     * @return column name
     */
    String columnName();

    /**
     * Return column type.
     *
     * @return Return column type
     */
    SqlType ormNextSqlType();

    /**
     * Return java sql type.
     *
     * @return java sql type
     * @see java.sql.Types
     */
    int sqlType();

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
    DataPersister dataPersister();

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
     * Return column type.
     *
     * @return column type
     */
    Class<?> getType();

    /**
     * Column length.
     *
     * @return column length
     */
    int length();

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
     * True for set NULL instead of default definition value.
     *
     * @return true for set NULL instead of default definition value
     */
    boolean defaultIfNull();

    /**
     * Is unique?
     *
     * @return true if unique
     */
    boolean unique();

    /**
     * If true column will be defined in create table.
     *
     * @return if true column will be defined in create table
     */
    boolean defineInCreateTable();

    /**
     * True if this instance {@link SimpleDatabaseColumnTypeImpl}.
     *
     * @return true if this instance {@link SimpleDatabaseColumnTypeImpl}
     */
    boolean databaseColumnType();

    /**
     * True if this instance {@link ForeignColumnTypeImpl}.
     *
     * @return true if this instance {@link ForeignColumnTypeImpl}
     */
    boolean foreignColumnType();

    /**
     * True if this instance {@link ForeignCollectionColumnTypeImpl}.
     *
     * @return true if this instance {@link ForeignCollectionColumnTypeImpl}
     */
    boolean foreignCollectionColumnType();
}
