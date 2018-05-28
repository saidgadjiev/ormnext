package ru.saidgadjiev.ormnext.core.field.data_persister;

import java.util.Arrays;
import java.util.List;

/**
 * Base database type that defines persistance methods for the various data types.
 */
public abstract class BaseDataPersister implements DataPersister {

    /**
     * Associated classes for this type.
     */
    private final Class<?>[] classes;

    /**
     * Create a new instance.
     * @param classes associated classes for this type
     */
    protected BaseDataPersister(Class<?>[] classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "classes=" + Arrays.toString(classes);
    }

    @Override
    public List<Class<?>> getAssociatedClasses() {
        return Arrays.asList(classes);
    }

}
