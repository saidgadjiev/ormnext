package ru.saidgadjiev.orm.next.core.db;

import ru.saidgadjiev.orm.next.core.field.persister.IntegerDataPersister;

public class SerialTypeDataPersister extends IntegerDataPersister {

    @Override
    public int getDataType() {
        return 8;
    }
}
