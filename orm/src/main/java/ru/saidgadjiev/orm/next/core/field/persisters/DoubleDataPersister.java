package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.literals.DoubleLiteral;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;

/**
 * Created by said on 03.02.2018.
 */
public class DoubleDataPersister extends BaseDataPersister {

    public DoubleDataPersister() {
        super(new Class[] { Double.class, double.class});
    }

    @Override
    public Object parseDefaultTo(IDBFieldType fieldType, String value) throws IllegalArgumentException {
        return Double.valueOf(value);
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

    @Override
    public Literal getLiteral(IDBFieldType fieldType, Object object) {
        return new DoubleLiteral((Double) object);
    }

    @Override
    public int getDataType() {
        return DataType.DOUBLE;
    }
}
