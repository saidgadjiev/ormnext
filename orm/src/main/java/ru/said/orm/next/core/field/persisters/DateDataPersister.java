package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.literals.Literal;
import ru.said.orm.next.core.query.core.literals.TimeLiteral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateDataPersister implements DataPersister {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class[] {Date.class, LocalDate.class, LocalDateTime.class};
    }

    @Override
    public Literal<Date> getLiteral(Object object) {
        return new TimeLiteral((Date) object);
    }

    @Override
    public DataType getDataType() {
        return null;
    }

    @Override
    public Object convertTo(String value) throws IllegalArgumentException {

        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
