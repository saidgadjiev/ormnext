package ru.saidgadjiev.ormnext.core.field.fieldtype;

public interface IForeignDatabaseColumnType extends IDatabaseColumnType {

    Class<?> getCollectionObjectClass();

    ForeignColumnKey getForeignColumnKey();

    String getForeignTableName();
}
