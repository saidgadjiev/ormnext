package ru.saidgadjiev.ormnext.core.field.persister;

public class SerialTypeDataPersister extends IntegerDataPersister {

    public static final int TYPE = 12;

    @Override
    public int getDataType() {
        return TYPE;
    }
}
