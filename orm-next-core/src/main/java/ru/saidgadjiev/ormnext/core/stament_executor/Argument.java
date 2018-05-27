package ru.saidgadjiev.ormnext.core.stament_executor;

/**
 * Statement argument info.
 */
public class Argument {

    /**
     * Data type.
     */
    private final int dataType;

    /**
     * Argument.
     */
    private final Object value;

    /**
     * Create a new argument.
     * @param dataType data type
     * @param value value
     */
    public Argument(int dataType, Object value) {
        this.dataType = dataType;
        this.value = value;
    }

    /**
     * Return data type.
     * @return data type
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Return value.
     * @return value
     */
    public Object getValue() {
        return value;
    }
}
