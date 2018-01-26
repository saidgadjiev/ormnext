package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.literals.Literal;
import ru.said.orm.next.core.query.core.literals.StringLiteral;

public class StringDataPersister implements DataPersister {
    @Override
    public Class<?>[] getAssociatedClasses() {
        return new Class<?>[] { String.class };
    }

    @Override
    public Literal<String> getLiteral(Object object) {
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
