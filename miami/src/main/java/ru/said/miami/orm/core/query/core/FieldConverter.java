package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.DataType;

public class FieldConverter {

    private static final FieldConverter INSTANSE = new FieldConverter();

    private FieldConverter() {}

    public static FieldConverter getInstanse() {
        return INSTANSE;
    }

    public RValue convert(DataType dataType, Object value) {
        switch (dataType) {
            case STRING:
                return new StringLiteral((String) value);
            case UNKNOWN:
                return null;
            default:
                return null;
        }
    }
}
