package ru.said.miami.orm.core.field.persisters;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.sqlQuery.Operand;

public interface DataPersister {

    Class<?>[] getAssociatedClasses();

    Operand getAssociatedOperand(Object object);

    DataType getDataType();
}
