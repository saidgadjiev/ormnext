package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.core.literals.TimeLiteral;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateDataPersister implements DataPersister {

    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class[] {Date.class, LocalDate.class, LocalDateTime.class};
    }

    @Override
    public Operand getAssociatedOperand(Object object) {
        return new TimeLiteral((Date) object);
    }

    @Override
    public DataType getDataType() {
        return null;
    }

    @Override
    public Object convertTo(String value) throws IllegalArgumentException {
        return null;
    }
}
