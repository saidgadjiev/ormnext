package ru.said.miami.orm.core.field.persisters;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.literals.StringLiteral;

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
}
