package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.exception.PersisterNotFoundException;
import ru.saidgadjiev.ormnext.core.field.datapersister.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Data persister manager. Holds data persister associated with data type.
 * Contain methods for resolve data persister by class or data type.
 *
 * @author said gadjiev
 */
public final class DataPersisterManager {

    /**
     * Registered data persisters.
     */
    private static Map<Integer, DataPersister> registeredPersisters = new HashMap<>();

    static {
        registeredPersisters.put(DataType.STRING, new StringDataPersister());
        registeredPersisters.put(DataType.INTEGER, new IntegerDataPersister());
        registeredPersisters.put(DataType.BOOLEAN, new BooleanDataPersister());
        registeredPersisters.put(DataType.LONG, new LongDataPersister());
        registeredPersisters.put(DataType.FLOAT, new FloatDataPersister());
        registeredPersisters.put(DataType.BYTE, new ByteDataPersister());
        registeredPersisters.put(DataType.SHORT, new ShortDataPersister());
        registeredPersisters.put(DataType.DATE, new DateDataPersister());
        registeredPersisters.put(DataType.TIME, new TimeDataPersister());
        registeredPersisters.put(DataType.TIMESTAMP, new TimeStampDataPersister());
        registeredPersisters.put(DataType.UNKNOWN, null);
    }

    /**
     * Util class can't be instantiated.
     */
    private DataPersisterManager() {
    }

    /**
     * Try find data persister for requested class.
     * Throw {@link PersisterNotFoundException} exception if persister not found.
     *
     * @param targetClazz target class
     * @return resolver data persister
     */
    public static DataPersister lookup(Class<?> targetClazz) {
        for (DataPersister persister : registeredPersisters.values()) {
            for (Class<?> clazz : persister.getAssociatedClasses()) {
                if (clazz == targetClazz) {
                    return persister;
                }
            }
        }

        throw new PersisterNotFoundException(targetClazz);
    }

    /**
     * Try find data persister for requested data type.
     * Throw {@link PersisterNotFoundException} exception if persister not found.
     *
     * @param type target data type
     * @return resolver data persister
     */
    public static DataPersister lookup(int type) {
        if (registeredPersisters.containsKey(type)) {
            return registeredPersisters.get(type);
        }

        throw new PersisterNotFoundException(type);
    }

    /**
     * Register a new or replace data persister for {@code type}.
     * @param type target data type
     * @param dataPersister target data persister
     */
    public static void register(int type, DataPersister dataPersister) {
        registeredPersisters.put(type, dataPersister);
    }
}
