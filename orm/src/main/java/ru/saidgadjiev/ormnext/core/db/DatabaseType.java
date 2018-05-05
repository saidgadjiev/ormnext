package ru.saidgadjiev.ormnext.core.db;

import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;

import static ru.saidgadjiev.ormnext.core.field.DataType.*;

public interface DatabaseType {

    String appendPrimaryKey(boolean generated);

    String appendNoColumn();

    default String getEntityNameEscape() {
        return "`";
    }

    default String getValueEscape() {
        return "'";
    }

    default String getTypeSqlPresent(AttributeDefinition def) {
        int dataType = def.getDataType();
        StringBuilder sql = new StringBuilder();

        switch (dataType) {
            case STRING:
            case DATE:
                sql.append("VARCHAR").append("(").append(def.getLength()).append(")");
                break;
            case INTEGER:
            case LONG:
                sql.append("INTEGER");
                break;
            case BOOLEAN:
                sql.append("BOOLEAN");
                break;
            case FLOAT:
                sql.append("FLOAT");
                break;
            case DOUBLE:
                sql.append("DOUBLE");
                break;
            case UNKNOWN:
                break;
        }

        return sql.toString();
    }

}
