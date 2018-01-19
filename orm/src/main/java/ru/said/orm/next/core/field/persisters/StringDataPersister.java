package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.Operand;
import ru.said.orm.next.core.query.core.literals.StringLiteral;
import ru.said.orm.next.core.field.DataType;

public class StringDataPersister implements DataPersister {
    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class<?>[] { String.class };
    }

    @Override
    public Operand getAssociatedOperand(Object object) {
        return new StringLiteral((String) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    @Override
    public Object convertTo(String value) throws IllegalArgumentException {
        return value;
    }
}
