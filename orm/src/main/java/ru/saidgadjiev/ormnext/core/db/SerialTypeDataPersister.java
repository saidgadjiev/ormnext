package ru.saidgadjiev.ormnext.core.db;

import ru.saidgadjiev.ormnext.core.field.persister.IntegerDataPersister;
import ru.saidgadjiev.ormnext.core.field.persister.IntegerDataPersister;

public class SerialTypeDataPersister extends IntegerDataPersister {

    @Override
    public int getDataType() {
        return 8;
    }
}
