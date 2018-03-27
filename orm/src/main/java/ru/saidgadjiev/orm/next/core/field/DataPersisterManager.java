package ru.saidgadjiev.orm.next.core.field;

import ru.saidgadjiev.orm.next.core.field.persisters.*;

import java.util.HashMap;
import java.util.Map;

public class DataPersisterManager {

    private static Map<Integer, DataPersister> registeredPersisters = new HashMap<>();

    static {
        registeredPersisters.put(DataType.STRING, new StringDataPersister());
        registeredPersisters.put(DataType.INTEGER, new IntegerDataPersister());
        registeredPersisters.put(DataType.BOOLEAN, new BooleanPersister());
        registeredPersisters.put(DataType.DATE, new DateStringDataPersister());
        registeredPersisters.put(DataType.LONG, new IntegerDataPersister());
        registeredPersisters.put(DataType.FLOAT, new FloatDataPersister());
        registeredPersisters.put(DataType.DOUBLE, new DoubleDataPersister());
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

        throw new IllegalArgumentException("Persister for field not found");
    }

    public static DataPersister<?> lookup(int type) {
        if (registeredPersisters.containsKey(type)) {
            return registeredPersisters.get(type);
        }

        throw new IllegalArgumentException("Persister for type " + type + " not found");
    }

    public static void register(int type, DataPersister<?> dataPersister) {
        registeredPersisters.put(type, dataPersister);
    }
}
