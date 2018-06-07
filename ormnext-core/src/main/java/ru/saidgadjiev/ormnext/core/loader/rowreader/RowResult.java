package ru.saidgadjiev.ormnext.core.loader.rowreader;

/**
 * Read database row result.
 *
 * @param <T> result type
 * @author Said Gadjiev
 */
public class RowResult<T> {

    /**
     * Read object id.
     */
    private final Object id;

    /**
     * Read result.
     */
    private final T result;

    /**
     * True if is a new result.
     */
    private final boolean isNew;

    /**
     * Create a new result.
     *
     * @param result target result
     * @param isNew  true if a new result
     * @param id     target read id
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

    /**
     * Return read object id.
     *
     * @return read object id
     */
    public Object getId() {
        return id;
    }
}
