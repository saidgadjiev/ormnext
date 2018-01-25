package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.literals.BooleanLiteral;
import ru.said.orm.next.core.query.core.literals.Literal;

public class BooleanPersister implements DataPersister {

    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class[] {Boolean.class, boolean.class };
    }

    @Override
    public Literal<Boolean> getLiteral(Object object) {
        return new BooleanLiteral((Boolean) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Boolean convertTo(String value) {
        return Boolean.valueOf(value);
    }
}
