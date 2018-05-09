package ru.saidgadjiev.ormnext.core.db;

public class SQLiteDatabaseType extends BaseDatabaseType {

    @Override
    public String getPrimaryKeyDefinition(boolean generated) {
        StringBuilder builder = new StringBuilder();

        builder.append(" PRIMARY KEY");
        if (generated) {
            builder.append(" AUTOINCREMENT");
        }

        return builder.toString();
    }

    @Override
    public String getNoArgsInsertDefinition() {
        return "DEFAULT VALUES";
    }
}
