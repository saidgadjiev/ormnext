package ru.saidgadjiev.ormnext.core.loader.rowreader;

/**
 * Read database row result.
 *
 * @param <T> result type
 * @author said gadjiev
 */
public class RowResult<T> {

    private final Object id;

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
     *
     * @param result result
     * @param isNew  true if a new result
     */
    public RowResult(Object id, T result, boolean isNew) {
        this.id = id;
        this.result = result;
        this.isNew = isNew;
    }

    /**
     * Return result.
     *
     * @return result
     */
    public T getResult() {
        return result;
    }

    /**
     * Return true if a new result.
     *
     * @return true if a new result
     */
    public boolean isNew() {
        return isNew;
    }

    public Object getId() {
        return id;
    }
}
