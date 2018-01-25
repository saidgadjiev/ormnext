package ru.said.orm.next.core.field.persisters;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.literals.Literal;

public interface DataPersister<T> extends FieldConverter<T> {

    Class<?>[] getAssociatedClasses();

    Literal<T> getLiteral(Object object);

    DataType getDataType();
}
