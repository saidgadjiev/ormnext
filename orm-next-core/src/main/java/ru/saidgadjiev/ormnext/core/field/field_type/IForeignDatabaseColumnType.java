package ru.saidgadjiev.ormnext.core.field.field_type;

import ru.saidgadjiev.ormnext.core.field.FetchType;

/**
 * This interface represent foreign database column type.
 * Which annotated with {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}
 * or {@link ru.saidgadjiev.ormnext.core.field.ForeignCollectionField}
 */
public interface IForeignDatabaseColumnType extends IDatabaseColumnType {

    /**
     * Return fetch type.
     * @return fetch type
     * @see FetchType
     */
    FetchType getFetchType();

    /**
     * Return column key. It is a pair of table name + column name.
     * @return column key
     * @see ColumnKey
     */
    ColumnKey getColumnKey();

    /**
     * Return foreign table name.
     * @return foreign table name
     */
    String getForeignTableName();

    /**
     * Return foreign column name.
     * @return foreign column name
     */
    String getForeignColumnName();
}
