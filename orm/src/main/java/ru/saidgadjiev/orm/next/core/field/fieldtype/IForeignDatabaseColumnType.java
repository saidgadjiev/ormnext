package ru.saidgadjiev.orm.next.core.field.fieldtype;

public interface IForeignDatabaseColumnType extends IDatabaseColumnType {

    Class<?> getForeignFieldClass();

    ForeignColumnType getForeignColumnType();

    ForeignColumnKey getForeignColumnKey();

    String getForeignTableName();
}
