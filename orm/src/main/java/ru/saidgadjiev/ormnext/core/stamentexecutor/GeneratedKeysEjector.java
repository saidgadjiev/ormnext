package ru.saidgadjiev.ormnext.core.stamentexecutor;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by said on 12.11.17.
 */
public class GeneratedKeysEjector {

    private Object generatedValue;

    private int type;

    public GeneratedKeysEjector(Object generatedValue, int type) {
        this.generatedValue = generatedValue;
        this.type = type;
    }

    public Number getGeneratedKey() throws SQLException {
        return getIdColumnData();
    }

    private Number getIdColumnData() throws SQLException {
         switch (type) {
            case Types.BIGINT :
            case Types.DECIMAL :
            case Types.NUMERIC :
                return (Long) generatedValue;
            case Types.INTEGER :
                return (Integer) generatedValue;
            default :
                throw new SQLException("Unknown DataType for typeVal " + type + " for " + generatedValue);
        }
    }
}
