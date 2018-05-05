package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.core.literals.DoubleLiteral;
import ru.saidgadjiev.ormnext.core.query.core.literals.Literal;

/**
 * Created by said on 03.02.2018.
 */
public class DoubleDataPersister extends BaseDataPersister {

    public DoubleDataPersister() {
        super(new Class[] { Double.class, double.class});
    }

    @Override
    public Object parseDefaultTo(IDatabaseColumnType fieldType, String value) throws IllegalArgumentException {
        return Double.valueOf(value);
    }

    @Override
    public Class<?>[] getAssociatedClasses() {
        return classes;
    }

    @Override
    public Literal getLiteral(IDatabaseColumnType fieldType, Object object) {
        return new DoubleLiteral((Double) object);
    }

    @Override
    public int getDataType() {
        return DataType.DOUBLE;
    }
}
