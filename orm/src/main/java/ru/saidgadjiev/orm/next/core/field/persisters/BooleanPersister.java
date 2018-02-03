package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.literals.BooleanLiteral;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;

public class BooleanPersister extends BaseDataPersister {

    public BooleanPersister() {
        super(new Class[] {Boolean.class, boolean.class });
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

    @Override
    public Literal<Boolean> getLiteral(IDBFieldType fieldType, Object object) {
        return new BooleanLiteral((Boolean) object);
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Boolean parseDefaultTo(IDBFieldType fieldType, String value) {
        return Boolean.valueOf(value);
    }
}
