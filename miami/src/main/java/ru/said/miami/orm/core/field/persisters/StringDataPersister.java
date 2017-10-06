package ru.said.miami.orm.core.field.persisters;

import ru.said.miami.orm.core.field.DataPersister;
import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.StringLiteral;

public class StringDataPersister implements DataPersister {
    @Override
    public Class<?> getAssociatedClass() {
        return String.class;
    }

    @Override
    public Operand getAssociatedOperand(Object object) {
        return new StringLiteral((String) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }
}
