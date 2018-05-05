package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.core.literals.Literal;
import ru.saidgadjiev.ormnext.core.query.core.literals.StringLiteral;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateStringDataPersister extends BaseDateDataPersister {

    public static final String DEFAULT_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public Literal<String> getLiteral(IDatabaseColumnType fieldType, Object object) {
        return new StringLiteral(object == null ? null : getFormatter(fieldType).format(object));
    }

    @Override
    public int getDataType() {
        return DataType.STRING;
    }

    @Override
    public Object parseDefaultTo(IDatabaseColumnType fieldType, String value) throws IllegalArgumentException {
        try {
            return getFormatter(fieldType).parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Object parseSqlToJava(IDatabaseColumnType fieldType, Object object) throws SQLException {
        if (object == null) {
            return null;
        }

        try {
            return getFormatter(fieldType).parse(String.valueOf(object));
        } catch (ParseException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public Object parseJavaToSql(IDatabaseColumnType fieldType, Object object) {
        if (object == null) {
            return null;
        }

        return getFormatter(fieldType).format(object);
    }

    private SimpleDateFormat getFormatter(IDatabaseColumnType fieldType) {
        String format = fieldType.getFormat();

        if (format != null && !format.isEmpty()) {
            return new SimpleDateFormat(format);
        }

        return new SimpleDateFormat(DEFAULT_FORMAT);
    }
}
