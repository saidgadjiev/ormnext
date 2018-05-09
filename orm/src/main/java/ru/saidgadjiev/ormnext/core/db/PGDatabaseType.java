package ru.saidgadjiev.ormnext.core.db;

import ru.saidgadjiev.ormnext.core.field.persister.SerialTypeDataPersister;
import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;

public class PGDatabaseType extends BaseDatabaseType {
    @Override
    public String getPrimaryKeyDefinition(boolean generated) {
        return " PRIMARY KEY";
    }

    @Override
    public String getNoArgsInsertDefinition() {
        return " DEFAULT VALUES";
    }

    @Override
    public String getEntityNameEscape() {
        return "\"";
    }

    @Override
    public String getTypeSqlPresent(AttributeDefinition attributeDefinition) {
        if (attributeDefinition.getDataType() == SerialTypeDataPersister.TYPE) {
            return "SERIAL";
        }

        return super.getTypeSqlPresent(attributeDefinition);
    }
}
