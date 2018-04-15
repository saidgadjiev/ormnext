package ru.saidgadjiev.orm.next.core.validator.data_persister;

import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;

/**
 * Created by said on 03.02.2018.
 */
public interface IValidator {

    void validate(DataPersister<?> dataPersister);
}
