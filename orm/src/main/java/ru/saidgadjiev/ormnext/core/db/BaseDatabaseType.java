package ru.saidgadjiev.ormnext.core.db;

import ru.saidgadjiev.ormnext.core.exception.UnknownTypeException;
import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;

import static ru.saidgadjiev.ormnext.core.field.DataType.*;
import static ru.saidgadjiev.ormnext.core.field.DataType.UNKNOWN;

public abstract class BaseDatabaseType implements DatabaseType {

    public String getTypeSqlPresent(AttributeDefinition def) {
        int dataType = def.getDataType();
        StringBuilder sql = new StringBuilder();

        switch (dataType) {
            case STRING:
                sql.append("VARCHAR").append("(").append(def.getLength()).append(")");
                break;
            case INTEGER:
                sql.append("INTEGER");
            case BOOLEAN:
                sql.append("BOOLEAN");
                break;
            case LONG:
                sql.append("BIGINT");
                break;
            case FLOAT:
                sql.append("FLOAT");
                break;
            case DOUBLE:
                sql.append("DOUBLE");
                break;
            case BYTE:
                sql.append("BYTE");
                break;
            case SHORT:
                sql.append("SHORT");
                break;
            case DATE:
                sql.append("DATE");
                break;
            case TIME:
                sql.append("TIME");
                break;
            case TIMESTAMP:
                sql.append("TIMESTAMP");
                break;
            default:
                throw new UnknownTypeException(dataType);
        }

        return sql.toString();
    }

    public String getEntityNameEscape() {
        return "`";
    }

    public String getValueEscape() {
        return "'";
    }
}
