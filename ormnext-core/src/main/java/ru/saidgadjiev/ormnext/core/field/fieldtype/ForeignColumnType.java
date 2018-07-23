package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.FetchType;

import java.lang.reflect.Field;

/**
 * This interface represent foreign database column type.
 * Which annotated with {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}
 * or {@link ru.saidgadjiev.ormnext.core.field.ForeignCollectionField}
 *
 * @author Said Gadjiev
 */
public interface ForeignColumnType extends DatabaseColumnType {

    /**
     * Is foreign auto create?
     *
     * @return true if foreign auto create
     */
    boolean foreignAutoCreate();

    /**
     * Return fetch type.
     *
     * @return fetch type
     * @see FetchType
     */
    FetchType getFetchType();

    /**
     * Return foreign table name.
     *
     * @return foreign table name
     */
    String getForeignTableName();

    /**
     * Return foreign column name.
     *
     * @return foreign column name
     */
    String getForeignColumnName();

    /**
     * Return foreign field.
     *
     * @return foreign field
     */
    Field getForeignField();

    /**
     * Return foreign column type.
     *
     * @return foreign column type
     */
    DatabaseColumnType getForeignColumnType();
}
