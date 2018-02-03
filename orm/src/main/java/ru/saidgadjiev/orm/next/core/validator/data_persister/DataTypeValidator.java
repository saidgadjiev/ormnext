package ru.saidgadjiev.orm.next.core.validator.data_persister;

import ru.saidgadjiev.orm.next.core.field.persisters.DataPersister;
import ru.saidgadjiev.orm.next.core.utils.ExceptionUtils;

import java.lang.reflect.Field;

/**
 * Created by said on 03.02.2018.
 */
public class DataTypeValidator implements IValidator {

    private final Field field;

    public DataTypeValidator(Field field) {
        this.field = field;
    }

    @Override
    public void validate(DataPersister<?> dataPersister) {
        if (!dataPersister.isValidForField(field)) {
            throw new IllegalArgumentException(
                    ExceptionUtils.message(ExceptionUtils.Exception.WRONG_TYPE, field.getName(), dataPersister.toString()));
        }
    }
}
