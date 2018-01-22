package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.Operand;

public interface DataPersister<T> extends FieldConverter<T> {

    Class<?>[] getAssociatedClasses();

    Operand getAssociatedOperand(Object object);

    DataType getDataType();
}
