package ru.said.miami.orm.core.field.persisters;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.literals.BooleanLiteral;
import ru.said.miami.orm.core.query.core.Operand;

public class BooleanPersister implements DataPersister {

    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class[] {Boolean.class, boolean.class };
    }

    @Override
    public Operand getAssociatedOperand(Object object) {
        return new BooleanLiteral((Boolean) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }
}
