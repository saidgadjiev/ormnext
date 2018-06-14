package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;

/**
 * Statement argument info.
 *
 * @author Said Gadjiev
 */
public class Argument {

    /**
     * Data persister.
     */
    private DataPersister dataPersister;

    /**
     * Argument.
     */
    private final Object value;

    /**
     * Create a new argument.
     * @param dataPersister target data persister
     * @param value value
     */
    public Argument(DataPersister dataPersister, Object value) {
        this.dataPersister = dataPersister;
        this.value = value;
    }

    /**
     * Return data type.
     * @return data type
     */
    public int getSqlType() {
        return dataPersister.getSqlType();
    }

    /**
     * Return value.
     * @return value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Data persister.
     *
     * @return data persister
     */
    public DataPersister getDataPersister() {
        return dataPersister;
    }

    @Override
    public String toString() {
        return "Argument{"
                + "dataPersister=" + dataPersister
                + ", value=" + value
                + '}';
    }
}
