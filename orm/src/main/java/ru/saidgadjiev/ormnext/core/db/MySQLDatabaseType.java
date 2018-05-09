package ru.saidgadjiev.ormnext.core.db;

/**
 * Created by said on 11.03.2018.
 */
public class MySQLDatabaseType extends BaseDatabaseType {
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
