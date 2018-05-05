package ru.saidgadjiev.ormnext.core.validator.datapersister;

import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;

/**
 * Created by said on 03.02.2018.
 */
public interface IValidator {

    void validate(DataPersister<?> dataPersister);
}
