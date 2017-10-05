package ru.said.miami.orm.core.field.persisters;

import ru.said.miami.orm.core.field.DataPersister;
import ru.said.miami.orm.core.query.core.IntLiteral;
import ru.said.miami.orm.core.query.core.Operand;

public class IntegerDataPersister implements DataPersister {

    @Override
    public Class<?> getAssociatedClass() {
        return Integer.class;
    }

    @Override
    public Operand getAssociatedOperand(Object object) {
        return new IntLiteral((Integer) object);
    }
}
