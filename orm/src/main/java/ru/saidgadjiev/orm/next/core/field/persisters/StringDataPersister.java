package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;
import ru.saidgadjiev.orm.next.core.query.core.literals.StringLiteral;

public class StringDataPersister extends BaseDataPersister {

    public StringDataPersister() {
        super(new Class<?>[] { String.class });
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

    @Override
    public Literal<String> getLiteral(IDBFieldType fieldType, Object object) {
        return new StringLiteral((String) object);
    }

    @Override
    public int getDataType() {
        return DataType.STRING;
    }

    @Override
    public Object parseDefaultTo(IDBFieldType fieldType, String value) throws IllegalArgumentException {
        return value;
    }
}
