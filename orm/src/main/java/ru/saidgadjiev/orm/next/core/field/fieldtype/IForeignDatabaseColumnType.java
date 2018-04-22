package ru.saidgadjiev.orm.next.core.field.fieldtype;

public interface IForeignDatabaseColumnType extends IDatabaseColumnType {

    Class<?> getForeignFieldClass();

    ForeignColumnKey getForeignColumnKey();

    String getForeignTableName();
}
