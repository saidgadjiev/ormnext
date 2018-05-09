package ru.saidgadjiev.ormnext.core.db;

public class H2DatabaseType extends BaseDatabaseType {

    @Override
    public String getPrimaryKeyDefinition(boolean generated) {
        StringBuilder builder = new StringBuilder();

        if (generated) {
            builder.append(" AUTO_INCREMENT");
        }
        builder.append(" PRIMARY KEY");

        return builder.toString();
    }

    @Override
    public String getNoArgsInsertDefinition() {
        return "() VALUES ()";
    }
}
