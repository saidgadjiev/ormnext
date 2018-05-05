package ru.saidgadjiev.ormnext.core.field.fieldtype;

public interface IForeignDatabaseColumnType extends IDatabaseColumnType {

    Class<?> getForeignFieldClass();

    ForeignColumnKey getForeignColumnKey();

    String getForeignTableName();
}
