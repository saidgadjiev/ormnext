package ru.saidgadjiev.orm.next.core.field;

import ru.saidgadjiev.orm.next.core.field.persisters.DataPersister;

import java.util.ArrayList;
import java.util.List;

public class DataPersisterManager {

    private static List<DataPersister> registeredPersisters = new ArrayList<>();

    static {
        for (DataType dataType: DataType.values()) {
            registeredPersisters.add(dataType.getDataPersister());
        }
    }

    private DataPersisterManager() {
    }

    public static DataPersister<?> lookup(Class<?> targetClazz) {
        for (DataPersister<?> persister : registeredPersisters) {
            for (Class<?> clazz: persister.getAssociatedClasses()) {
                if (clazz == targetClazz) {
                    return persister;
                }
            }
        }

        throw new IllegalArgumentException("Persister for field not found");
    }
}
