package ru.said.orm.next.core.db;

public class H2DatabaseType implements DatabaseType {

    @Override
    public void appendPrimaryKey(StringBuilder sql, boolean generated) {
        if (generated) {
            sql.append(" AUTO_INCREMENT");
        }
        sql.append(" PRIMARY KEY");
    }

    @Override
    public String getDatabaseName() {
        return "h2";
    }

    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }
}
