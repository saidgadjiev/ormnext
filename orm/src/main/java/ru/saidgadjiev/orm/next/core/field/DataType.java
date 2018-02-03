package ru.saidgadjiev.orm.next.core.field;

import ru.saidgadjiev.orm.next.core.field.persisters.*;

public enum DataType {
    STRING(new StringDataPersister()),
    INTEGER(new IntegerDataPersister()),
    BOOLEAN(new BooleanPersister()),
    DATE(new DateStringDataPersister()),
    LONG(new IntegerDataPersister()),
    FLOAT(new FloatDataPersister()),
    DOUBLE(new DoubleDataPersister()),
    UNKNOWN(null);

    private DataPersister<?> dataPersister;

    DataType(DataPersister dataPersister) {
        this.dataPersister = dataPersister;
    }

    public DataPersister<?> getDataPersister() {
        return dataPersister;
    }
}
