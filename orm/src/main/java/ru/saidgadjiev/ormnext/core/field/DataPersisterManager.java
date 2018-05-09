package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.exception.PersisterNotFoundException;
import ru.saidgadjiev.ormnext.core.field.persister.*;
import ru.saidgadjiev.ormnext.core.field.persister.*;

import java.util.HashMap;
import java.util.Map;

public class DataPersisterManager {

    private static Map<Integer, DataPersister> registeredPersisters = new HashMap<>();

    static {
        registeredPersisters.put(DataType.STRING, new StringDataPersister());
        registeredPersisters.put(DataType.INTEGER, new IntegerDataPersister());
        registeredPersisters.put(DataType.BOOLEAN, new BooleanPersister());
        registeredPersisters.put(DataType.LONG, new LongDataPersister());
        registeredPersisters.put(DataType.FLOAT, new FloatDataPersister());
        registeredPersisters.put(DataType.BYTE, new ByteDataPersister());
        registeredPersisters.put(DataType.SHORT, new ShortDataPersister());
        registeredPersisters.put(DataType.DATE, new DateDataPersister());
        registeredPersisters.put(DataType.TIME, new TimeDataPersister());
        registeredPersisters.put(DataType.TIMESTAMP, new TimeStampDataPersister());
        registeredPersisters.put(DataType.UNKNOWN, null);
    }

    private DataPersisterManager() {
    }

    public static DataPersister<?> lookup(Class<?> targetClazz) {
        for (DataPersister<?> persister : registeredPersisters.values()) {
            for (Class<?> clazz: persister.getAssociatedClasses()) {
                if (clazz == targetClazz) {
                    return persister;
                }
            }
        }

        throw new PersisterNotFoundException(targetClazz);
    }

    public static DataPersister<?> lookup(int type) {
        if (registeredPersisters.containsKey(type)) {
            return registeredPersisters.get(type);
        }

        throw new PersisterNotFoundException(type);
    }

    public static void register(int type, DataPersister<?> dataPersister) {
        registeredPersisters.put(type, dataPersister);
    }
}
