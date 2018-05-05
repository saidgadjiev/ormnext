package ru.saidgadjiev.ormnext.core.field.persister;

import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;
import ru.saidgadjiev.ormnext.core.query.core.literals.Literal;
import ru.saidgadjiev.ormnext.core.query.core.AttributeDefinition;

import java.lang.reflect.Field;

public interface DataPersister<T> extends FieldConverter<T> {

    default boolean isValidForGenerated() {
        return false;
    }

    boolean isValidForField(Field field);

    Class<?>[] getAssociatedClasses();

    Literal<T> getLiteral(IDatabaseColumnType fieldType, Object object);

    int getDataType();

    default Object convertIdNumber(Number value) {
        return null;
    }

    default String typeToSql(int type, AttributeDefinition attributeDefinition) {
        return null;
    }
}
