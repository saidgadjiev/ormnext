package ru.saidgadjiev.ormnext.benchmark.domain;

import ru.saidgadjiev.ormnext.core.field.data_persister.ColumnConverter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Timestamp to date converter / date to timestamp converter.
 *
 * @author said gadjiev
 */
public class TimestampToDate implements ColumnConverter<Timestamp, Date> {

    @Override
    public Timestamp javaToSql(Date value) {
        return new Timestamp(value.getTime());
    }

    @Override
    public Date sqlToJava(Timestamp value) {
        return new Date(value.getTime());
    }
}
