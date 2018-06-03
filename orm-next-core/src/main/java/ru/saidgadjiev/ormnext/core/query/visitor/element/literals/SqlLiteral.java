package ru.saidgadjiev.ormnext.core.query.visitor.element.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class SqlLiteral implements Literal<String> {

    private final String  value;

    private final boolean needEscape;

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

    public boolean isNeedEscape() {
        return needEscape;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
