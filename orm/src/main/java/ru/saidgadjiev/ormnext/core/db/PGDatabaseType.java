package ru.saidgadjiev.ormnext.core.db;

import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;
import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;

public class PGDatabaseType implements DatabaseType {
    @Override
    public String appendPrimaryKey(boolean generated) {
        StringBuilder builder = new StringBuilder();

        builder.append(" PRIMARY KEY");

        return builder.toString();
    }

    @Override
    public String appendNoColumn() {
        return " DEFAULT VALUES";
    }

    @Override
    public String getEntityNameEscape() {
        return "\"";
    }

    @Override
    public String getTypeSqlPresent(AttributeDefinition attributeDefinition) {
        if (attributeDefinition.getDataType() == 8) {
            return "SERIAL";
        }

        return DatabaseType.super.getTypeSqlPresent(attributeDefinition);
    }
}
