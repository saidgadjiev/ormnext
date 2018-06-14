package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.field.datapersister.*;

/**
 * This class holds default orm next data types.
 * You can add new data type or override default types with associate any integer and register persister for him.
 *
 * @author Said Gadjiev
 * @see DataPersisterManager
 * @see ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister
 */
public enum DataType {

    /**
     * String type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.StringDataPersister
     */
    STRING(new StringDataPersister()),

    /**
     * Integer type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.IntegerDataPersister
     */
    INTEGER(new IntegerDataPersister()),

    /**
     * Boolean type.
     *
     * @see BooleanDataPersister
     */
    BOOLEAN(new BooleanDataPersister()),

    /**
     * Long type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.LongDataPersister
     */
    LONG(new LongDataPersister()),

    /**
     * Float type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.FloatDataPersister
     */
    FLOAT(new FloatDataPersister()),

    /**
     * Double type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.DoubleDataPersister
     */
    DOUBLE(new DoubleDataPersister()),

    /**
     * Byte type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.ByteDataPersister
     */
    BYTE(new ByteDataPersister()),

    /**
     * Short type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.ShortDataPersister
     */
    SHORT(new ShortDataPersister()),

    /**
     * Date type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.DateDataPersister
     */
    DATE(new DateDataPersister()),

    /**
     * Time type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.TimeDataPersister
     */
    TIME(new TimeDataPersister()),

    /**
     * Time stamp type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.TimeStampDataPersister
     */
    TIMESTAMP(new TimeStampDataPersister()),

    /**
     * Other data type.
     */
    OTHER(null);

    /**
     * Associated data persister.
     */
    private DataPersister dataPersister;

    /**
     * Constructor.
     *
     * @param dataPersister associated data persister
     */
    DataType(DataPersister dataPersister) {
        this.dataPersister = dataPersister;
    }

    /**
     * Return associated data persister.
     *
     * @return associated data persister
     */
    public DataPersister getDataPersister() {
        return dataPersister;
    }
}
