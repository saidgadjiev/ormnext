package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

public class ResultSetValue {

    private final Object value;

    private final boolean wasNull;

    public ResultSetValue(Object value, boolean wasNull) {
        this.value = value;
        this.wasNull = wasNull;
    }

    public Object getValue() {
        return value;
    }

    public boolean isWasNull() {
        return wasNull;
    }
}
