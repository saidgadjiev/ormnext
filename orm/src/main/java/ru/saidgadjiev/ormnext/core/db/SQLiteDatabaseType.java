package ru.saidgadjiev.ormnext.core.db;

public class SQLiteDatabaseType implements DatabaseType {

    @Override
    public String appendPrimaryKey(boolean generated) {
        StringBuilder builder = new StringBuilder();

        builder.append(" PRIMARY KEY");
        if (generated) {
            builder.append(" AUTOINCREMENT");
        }

        return builder.toString();
    }

    @Override
    public String appendNoColumn() {
        return "DEFAULT VALUES";
    }
}
