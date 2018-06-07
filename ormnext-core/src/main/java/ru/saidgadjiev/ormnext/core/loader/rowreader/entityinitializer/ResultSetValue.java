package ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer;

/**
 * Value retrieved from result set.
 *
 * @author Said Gadjiev
 */
public class ResultSetValue {

    /**
     * Retrieved value from result set.
     */
    private final Object value;

    /**
     * True if retrieved value was sql null.
     */
    private final boolean wasNull;

    /**
     * Create a new instance.
     *
     * @param value   target value
     * @param wasNull true if it is sql null
     */
    public ResultSetValue(Object value, boolean wasNull) {
        this.value = value;
        this.wasNull = wasNull;
    }

    /**
     * Return current value.
     *
     * @return current value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Is value was sql null?
     *
     * @return true if it is was null
     */
    public boolean isWasNull() {
        return wasNull;
    }

    @Override
    public String toString() {
        return "ResultSetValue{"
                + "value=" + value
                + ", wasNull=" + wasNull
                + '}';
    }
}
