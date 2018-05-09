package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;

import java.util.List;
import java.util.Optional;

public class Argument {

    private final List<Converter<?, Object>> converters;

    private final DataPersister<?> dataPersister;

    private final Object value;

    public Argument(List<Converter<?, Object>> converters, DataPersister<?> dataPersister, Object value) {
        this.converters = converters;
        this.dataPersister = dataPersister;
        this.value = value;
    }

    public Optional<List<Converter<?, Object>>> getConverter() {
        return Optional.ofNullable(converters);
    }

    public DataPersister<?> getDataPersister() {
        return dataPersister;
    }

    public Object getValue() {
        return value;
    }

    public static Argument from(IDatabaseColumnType columnType, Object arg) {
        return new Argument(columnType.getConverters().orElse(null), columnType.getDataPersister(), arg);
    }
}
