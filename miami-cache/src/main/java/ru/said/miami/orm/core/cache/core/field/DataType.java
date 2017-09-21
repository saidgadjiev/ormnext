package ru.said.miami.orm.core.cache.core.field;

/**
 * Created by said on 26.02.17.
 */
public enum DataType {
    STRING("varchar(255)"),
    INTEGER("INTEGER"),
    LONG("INTEGER"),
    UNKNOWN("");

    private final String type;

    DataType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
