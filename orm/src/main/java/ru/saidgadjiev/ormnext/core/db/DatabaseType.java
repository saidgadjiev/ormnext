package ru.saidgadjiev.ormnext.core.db;

import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;

import static ru.saidgadjiev.ormnext.core.field.DataType.*;

public interface DatabaseType {

    String getPrimaryKeyDefinition(boolean generated);

    String getNoArgsInsertDefinition();

    String getEntityNameEscape();

    String getValueEscape();

    String getTypeSqlPresent(AttributeDefinition def);
}
