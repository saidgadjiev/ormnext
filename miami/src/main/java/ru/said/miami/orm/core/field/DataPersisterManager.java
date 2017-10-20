package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.field.persisters.IntegerDataPersister;
import ru.said.miami.orm.core.field.persisters.StringDataPersister;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DataPersisterManager {

    private static List<DataPersister> registeredPersisters = new ArrayList<>();

    static {
        registeredPersisters.add(new IntegerDataPersister());
        registeredPersisters.add(new StringDataPersister());
    }

    private DataPersisterManager() {
    }

    public static DataPersister lookupField(Field field) {
        for (DataPersister persister : registeredPersisters) {
            for (Class<?> clazz: persister.getAssociatedClasses()) {
                if (clazz == field.getType()) {
                    return persister;
                }
            }
        }

        throw new IllegalArgumentException("Persister for field not found");
    }
}
