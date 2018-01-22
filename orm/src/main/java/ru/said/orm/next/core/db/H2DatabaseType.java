package ru.said.orm.next.core.db;

public class H2DatabaseType implements DatabaseType {

    @Override
    public void appendPrimaryKey(StringBuilder sql, boolean generated) {
        if (generated) {
            sql.append(" AUTO_INCREMENT");
        }
        sql.append(" PRIMARY KEY");
    }
}
