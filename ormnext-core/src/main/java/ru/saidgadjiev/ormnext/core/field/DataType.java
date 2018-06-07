package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.field.datapersister.BooleanDataPersister;

/**
 * This class holds default orm next data types.
 * You can add new data type or override default types with associate any integer and register persister for him.
 *
 * @author Said Gadjiev
 * @see DataPersisterManager
 * @see ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister
 */
public final class DataType {

    /**
     * Can't be instantiated.
     */
    private DataType() {
    }

    /**
     * String type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.StringDataPersister
     */
    public static final int STRING = 0;

    /**
     * Integer type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.IntegerDataPersister
     */
    public static final int INTEGER = 1;

    /**
     * Boolean type.
     *
     * @see BooleanDataPersister
     */
    public static final int BOOLEAN = 2;

    /**
     * Long type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.LongDataPersister
     */
    public static final int LONG = 3;

    /**
     * Float type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.FloatDataPersister
     */
    public static final int FLOAT = 4;

    /**
     * Double type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.DoubleDataPersister
     */
    public static final int DOUBLE = 5;

    /**
     * Byte type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.ByteDataPersister
     */
    public static final int BYTE = 6;

    /**
     * Short type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.ShortDataPersister
     */
    public static final int SHORT = 7;

    /**
     * Date type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.DateDataPersister
     */
    public static final int DATE = 8;

    /**
     * Time type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.TimeDataPersister
     */
    public static final int TIME = 9;

    /**
     * Time stamp type.
     *
     * @see ru.saidgadjiev.ormnext.core.field.datapersister.TimeStampDataPersister
     */
    public static final int TIMESTAMP = 10;

    /**
     * Unknown type.
     */
    public static final int UNKNOWN = 11;
}
