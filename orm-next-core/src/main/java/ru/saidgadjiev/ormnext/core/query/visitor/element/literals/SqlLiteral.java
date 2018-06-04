package ru.saidgadjiev.ormnext.core.query.visitor.element.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Literal which value will be directly append to sql query.
 *
 * @author said gadjiev
 */
public class SqlLiteral implements Literal<String> {

    /**
     * Value.
     */
    private final String value;

    /**
     * True if value need escape.
     */
    private final boolean needEscape;

    /**
     * Create a new instance.
     *
     * @param value      target value
     * @param needEscape true if need escape
     */
    public SqlLiteral(String value, boolean needEscape) {
        this.value = value;
        this.needEscape = needEscape;
    }

    @Override
    public String getOriginal() {
        return value;
    }

    @Override
    public String get() {
        return value;
    }

    /**
     * Is need escape?
     *
     * @return true if need escape
     */
    public boolean isNeedEscape() {
        return needEscape;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
