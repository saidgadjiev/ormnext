package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 08.09.17.
 */
public class StringLiteral implements Literal<String> {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return value;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
