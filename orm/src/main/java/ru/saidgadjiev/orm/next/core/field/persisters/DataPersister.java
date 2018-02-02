package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;

public interface DataPersister<T> extends FieldConverter<T> {

    Class<?>[] getAssociatedClasses();

    Literal<T> getLiteral(Object object);

    DataType getDataType();

    default Object convertIdNumber(Number value) {
        return null;
    }
}
