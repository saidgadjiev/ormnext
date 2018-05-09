package ru.saidgadjiev.ormnext.core.field;

import java.sql.SQLType;

public interface BaseType extends SQLType {
    @Override
    default String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    default String getVendor() {
        throw new UnsupportedOperationException();
    }
}
