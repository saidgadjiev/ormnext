package ru.saidgadjiev.orm.next.core.field.persister;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDatabaseColumnType;
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
    public Literal<Integer> getLiteral(IDatabaseColumnType fieldType, Object object) {
        return new IntLiteral((Integer) object);
    }

    @Override
    public int getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public Object parseDefaultTo(IDatabaseColumnType fieldType, String value) throws IllegalArgumentException {
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
