package ru.saidgadjiev.ormnext.core.field;

/**
 * References action for on update and on delete.
 *
 * @author Said Gadjiev
 */
public enum ReferenceAction {

    /**
     * No action.
     */
    NO_ACTION("NO ACTION"),

    /**
     * Restrict.
     */
    RESTRICT("RESTRICT"),

    /**
     * Set null.
     */
    SET_NULL("SET NULL"),

    /**
     * Set default.
     */
    SET_DEFAULT("SET DEFAULT"),

    /**
     * Cascade.
     */
    CASCADE("CASCADE");

    /**
     * Sql presentation.
     */
    private String sql;

    /**
     * Constructor.
     *
     * @param sql sql presentation.
     */
    ReferenceAction(String sql) {
        this.sql = sql;
    }

    /**
     * Return current sql presentation.
     *
     * @return sql presentation
     */
    public String getSql() {
        return sql;
    }
}
