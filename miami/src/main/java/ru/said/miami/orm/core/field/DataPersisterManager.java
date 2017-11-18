package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.field.persisters.BooleanPersister;
import ru.said.miami.orm.core.field.persisters.DataPersister;
import ru.said.miami.orm.core.field.persisters.IntegerDataPersister;
import ru.said.miami.orm.core.field.persisters.StringDataPersister;

import java.util.ArrayList;
import java.util.List;

public class DataPersisterManager {

    private static List<DataPersister> registeredPersisters = new ArrayList<>();

    static {
        registeredPersisters.add(new IntegerDataPersister());
        registeredPersisters.add(new StringDataPersister());
        registeredPersisters.add(new BooleanPersister());
    }

    private DataPersisterManager() {
    }

    public static DataPersister lookup(Class<?> targetClazz) {
        for (DataPersister persister : registeredPersisters) {
            for (Class<?> clazz: persister.getAssociatedClasses()) {
                if (clazz == targetClazz) {
                    return persister;
                }
            }
        }

        throw new IllegalArgumentException("Persister for field not found");
    }
}
