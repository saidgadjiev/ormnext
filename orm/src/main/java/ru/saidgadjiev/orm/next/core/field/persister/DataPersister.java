package ru.saidgadjiev.orm.next.core.field.persister;

import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;

import java.lang.reflect.Field;

public interface DataPersister<T> extends FieldConverter<T> {

    default boolean isValidForGenerated() {
        return false;
    }

    boolean isValidForField(Field field);

    Class<?>[] getAssociatedClasses();

    Literal<T> getLiteral(IDatabaseColumnType fieldType, Object object);

    int getDataType();

    default Object convertIdNumber(Number value) {
        return null;
    }
}
