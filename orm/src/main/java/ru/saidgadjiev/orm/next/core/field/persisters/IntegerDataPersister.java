package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.literals.IntLiteral;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;

public class IntegerDataPersister extends BaseDataPersister {

    public IntegerDataPersister() {
        super(new Class<?>[] {Integer.class, int.class, Long.class, long.class});
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

    @Override
    public Literal<Integer> getLiteral(IDBFieldType fieldType, Object object) {
        return new IntLiteral((Integer) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public Object parseDefaultTo(IDBFieldType fieldType, String value) throws IllegalArgumentException {
        return Integer.valueOf(value);
    }

    @Override
    public Object convertIdNumber(Number value) {
        return value.intValue();
    }

    @Override
    public boolean isValidForGenerated() {
        return true;
    }
}
