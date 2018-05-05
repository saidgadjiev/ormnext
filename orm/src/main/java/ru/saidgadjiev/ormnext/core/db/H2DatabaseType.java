package ru.saidgadjiev.ormnext.core.db;

public class H2DatabaseType implements DatabaseType {

    @Override
    public String appendPrimaryKey(boolean generated) {
        StringBuilder builder = new StringBuilder();

        if (generated) {
            builder.append(" AUTO_INCREMENT");
        }
        builder.append(" PRIMARY KEY");

        return builder.toString();
    }

    @Override
    public String appendNoColumn() {
        return "() VALUES ()";
    }
}
