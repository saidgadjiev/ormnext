package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.query.core.Operand;

public interface DataPersister {

    Class<?> getAssociatedClass();

    Operand getAssociatedOperand(Object object);

    DataType getDataType();
}
