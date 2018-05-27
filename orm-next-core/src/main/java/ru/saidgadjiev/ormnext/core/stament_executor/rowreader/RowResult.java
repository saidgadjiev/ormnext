package ru.saidgadjiev.ormnext.core.stament_executor.rowreader;

/**
 * Read database row result.
 * @param <T> result type
 */
public class RowResult<T> {

    /**
     * Readed result.
     */
    private final T result;

    /**
     * True if is a new result.
     */
    private final boolean isNew;

    /**
     * Create a new result.
     * @param result result
     * @param isNew true if a new result
     */
    public RowResult(T result, boolean isNew) {
        this.result = result;
        this.isNew = isNew;
    }

    /**
     * Return result.
     * @return result
     */
    public T getResult() {
        return result;
    }

    /**
     * Return true if a new result.
     * @return true if a new result
     */
    public boolean isNew() {
        return isNew;
    }
}
