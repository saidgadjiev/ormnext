package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.literals.IntLiteral;
import ru.said.orm.next.core.query.core.literals.Literal;

public class IntegerDataPersister implements DataPersister {

    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class<?>[] {Integer.class, int.class, Long.class, long.class};
    }

    @Override
    public Literal<Integer> getLiteral(Object object) {
        return new IntLiteral((Integer) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public Object convertTo(String value) throws IllegalArgumentException {
        return Integer.valueOf(value);
    }

    @Override
    public Object convertIdNumber(Number value) {
        return (Integer) value.intValue();
    }
}
