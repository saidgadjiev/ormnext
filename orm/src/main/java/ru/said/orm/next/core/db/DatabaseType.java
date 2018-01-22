package ru.said.orm.next.core.db;

public interface DatabaseType {

    void appendPrimaryKey(StringBuilder sql, boolean generated);
}
