package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;
import ru.saidgadjiev.orm.next.core.query.core.literals.StringLiteral;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateStringDataPersister extends BaseDateDataPersister {

    private static final String DEFAULT_FORMAT = "dd.MM.yyyy HH:mm:ss";

    @Override
    public Literal<String> getLiteral(IDBFieldType fieldType, Object object) {
        return new StringLiteral(object == null ? null : getFormatter(fieldType).format(object));
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public Object parseDefaultTo(IDBFieldType fieldType, String value) throws IllegalArgumentException {
        try {
            return getFormatter(fieldType).parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Object parseSqlToJava(IDBFieldType fieldType, Object object) throws Exception {
        if (object == null) {
            return null;
        }

        return getFormatter(fieldType).parse(String.valueOf(object));
    }

    @Override
    public Object parseJavaToSql(IDBFieldType fieldType, Object object) {
        if (object == null) {
            return null;
        }

        return getFormatter(fieldType).format(object);
    }

    private SimpleDateFormat getFormatter(IDBFieldType fieldType) {
        String format = fieldType.getFormat();

        if (format != null && !format.isEmpty()) {
            return new SimpleDateFormat(format);
        }

        return new SimpleDateFormat(DEFAULT_FORMAT);
    }
}
