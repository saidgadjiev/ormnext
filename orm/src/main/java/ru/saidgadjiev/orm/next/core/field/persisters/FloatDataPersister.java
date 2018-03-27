package ru.saidgadjiev.orm.next.core.field.persisters;

import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.literals.FloatLiteral;
import ru.saidgadjiev.orm.next.core.query.core.literals.Literal;

/**
 * Created by said on 03.02.2018.
 */
public class FloatDataPersister extends BaseDataPersister {

    public FloatDataPersister() {
        super(new Class[] {Float.class, float.class});
    }

    @Override
    public Object parseDefaultTo(IDBFieldType fieldType, String value) throws IllegalArgumentException {
        return Float.valueOf(value);
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

    @Override
    public Literal getLiteral(IDBFieldType fieldType, Object object) {
        return new FloatLiteral((Float) object);
    }

    @Override
    public int getDataType() {
        return DataType.FLOAT;
    }
}
