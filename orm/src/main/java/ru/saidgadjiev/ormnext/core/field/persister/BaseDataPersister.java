package ru.saidgadjiev.ormnext.core.field.persister;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by said on 03.02.2018.
 */
public abstract class BaseDataPersister<T> implements DataPersister<T> {

    private Class<?>[] classes;

    public BaseDataPersister(Class<?> [] classes) {
        this.classes = classes;
    }

    @Override
    public Object parseDefaultTo(String value) {
        return value;
    }

    @Override
    public boolean isValidForGenerated() {
        return false;
    }

    @Override
    public String toString() {
        return "classes=" + Arrays.toString(classes);
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

}
