package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.exception.PersisterNotFoundException;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;

import java.util.HashMap;
import java.util.Map;

/**
 * Data persister manager. Holds data persister associated with data type.
 * Contain methods for resolve data persister by class or data type.
 *
 * @author Said Gadjiev
 */
public final class DataPersisterManager {

    /**
     * Registered data persisters.
     */
    private static final Map<Class<?>, DataPersister> REGISTERED_PERSISTERS = new HashMap<>();

    static {
        for (DataType dataType: DataType.values()) {
            if (dataType.equals(DataType.UNKNOWN)) {
                continue;
            }
            dataType.getDataPersister()
                    .getAssociatedClasses()
                    .forEach(aClass -> REGISTERED_PERSISTERS.put(aClass, dataType.getDataPersister()));
        }
    }

    /**
     * Util class can't be instantiated.
     */
    private DataPersisterManager() { }

    /**
     * Try find data persister for requested class.
     * Throw {@link PersisterNotFoundException} exception if persister not found.
     *
     * @param targetClazz target class
     * @return resolver data persister
     */
    public static DataPersister lookup(Class<?> targetClazz) {
        for (DataPersister persister : REGISTERED_PERSISTERS.values()) {
            for (Class<?> clazz : persister.getAssociatedClasses()) {
                if (clazz == targetClazz) {
                    return persister;
                }
            }
        }

        throw new PersisterNotFoundException(targetClazz);
    }

    /**
     * Register a new or replace data persister.
     *
     * @param dataPersister target data persister
     */
    public static void register(DataPersister dataPersister) {
        dataPersister.getAssociatedClasses()
                .forEach(aClass -> REGISTERED_PERSISTERS.put(aClass, dataPersister));
    }
}
