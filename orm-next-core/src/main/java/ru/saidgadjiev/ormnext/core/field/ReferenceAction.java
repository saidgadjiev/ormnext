package ru.saidgadjiev.ormnext.core.field;

public enum ReferenceAction {

    NO_ACTION("NO ACTION"),

    RESTRICT("RESTRICT"),

    SET_NULL("SET NULL"),

    SET_DEFAULT("SET DEFAULT"),

    CASCADE("CASCADE");

    private String sql;

    ReferenceAction(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
