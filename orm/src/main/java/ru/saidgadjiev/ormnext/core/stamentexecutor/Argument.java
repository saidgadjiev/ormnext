package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;

import java.util.Optional;

public class Argument {

    private final Converter<?, ?> converter;

    private final DataPersister<?> dataPersister;

    private final Object value;

    public Argument(Converter<?, ?> converter, DataPersister<?> dataPersister, Object value) {
        this.converter = converter;
        this.dataPersister = dataPersister;
        this.value = value;
    }

    public Optional<Converter<?, ?>> getConverter() {
        return Optional.ofNullable(converter);
    }

    public DataPersister<?> getDataPersister() {
        return dataPersister;
    }

    public Object getValue() {
        return value;
    }

    public static Argument from(IDatabaseColumnType columnType, Object arg) {
        return new Argument(columnType.getConverter().orElse(null), columnType.getDataPersister(), arg);
    }
}
