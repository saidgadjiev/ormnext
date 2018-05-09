package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.persister.IntegerDataPersister;
import ru.saidgadjiev.ormnext.core.field.persister.IntegerDataPersister;

public class SerialTypeDataPersister extends IntegerDataPersister {

    public static final int TYPE = 12;

    @Override
    public int getDataType() {
        return TYPE;
    }
}
