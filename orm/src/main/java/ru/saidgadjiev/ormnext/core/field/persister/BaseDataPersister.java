package ru.saidgadjiev.ormnext.core.field.persister;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by said on 03.02.2018.
 */
public abstract class BaseDataPersister implements DataPersister {

    protected Class<?>[] classes;

    public BaseDataPersister(Class<?> [] classes) {
        this.classes = classes;
    }

    @Override
    public boolean isValidForField(Field field) {
        Class<?> fieldClazz = field.getType();

        for (Class<?> clazz: classes) {
            if (clazz.isAssignableFrom(fieldClazz)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "classes=" + Arrays.toString(classes);
    }
}
