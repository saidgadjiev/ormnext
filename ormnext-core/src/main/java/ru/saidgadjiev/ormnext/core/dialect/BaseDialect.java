package ru.saidgadjiev.ormnext.core.dialect;

import ru.saidgadjiev.ormnext.core.exception.UnknownTypeException;
import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;

import static ru.saidgadjiev.ormnext.core.field.DataType.*;

/**
 * Base class for all of the {@link Dialect} classes that provide the per-database type functionality to create
 * tables and build queries.
 *
 * @author Said Gadjiev
 */
public abstract class BaseDialect implements Dialect {

    @Override
    public String getTypeSqlPresent(AttributeDefinition def) {
        int dataType = def.getDataType();
        StringBuilder sql = new StringBuilder();

        switch (dataType) {
            case STRING:
                sql.append("VARCHAR").append("(").append(def.getLength()).append(")");
                break;
            case INTEGER:
                sql.append("INTEGER");
                break;
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

    @Override
    public String getEntityNameEscape() {
        return "`";
    }

    @Override
    public String getValueEscape() {
        return "'";
    }

    @Override
    public boolean supportTableForeignConstraint() {
        return true;
    }

    @Override
    public boolean supportTableUniqueConstraint() {
        return true;
    }
}
