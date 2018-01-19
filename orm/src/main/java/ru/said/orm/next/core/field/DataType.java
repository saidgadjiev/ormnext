package ru.said.orm.next.core.field;

import ru.said.orm.next.core.field.persisters.*;

public enum DataType {
    STRING(new StringDataPersister()),
    INTEGER(new IntegerDataPersister()),
    BOOLEAN(new BooleanPersister()),
    DATE(new DateDataPersister()),
    LONG(new IntegerDataPersister()),
    UNKNOWN(null);

    private DataPersister<?> dataPersister;

    DataType(DataPersister dataPersister) {
        this.dataPersister = dataPersister;
    }

    public DataPersister<?> getDataPersister() {
        return dataPersister;
    }
}
