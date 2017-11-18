package ru.said.miami.orm.core.field.persisters;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.literals.IntLiteral;
import ru.said.miami.orm.core.query.core.Operand;

public class IntegerDataPersister implements DataPersister {

    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class<?>[] {Integer.class, int.class};
    }

    @Override
    public Operand getAssociatedOperand(Object object) {
        return new IntLiteral((Integer) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }
}
