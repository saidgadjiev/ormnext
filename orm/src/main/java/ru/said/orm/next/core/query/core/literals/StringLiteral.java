package ru.said.orm.next.core.query.core.literals;

import ru.said.orm.next.core.query.visitor.QueryVisitor;

/**
 * Created by said on 08.09.17.
 */
public class StringLiteral implements Literal<String> {

    private String value;

    private String original;

    public StringLiteral(String value) {
        this.value = value;
        this.original = value == null ? null :  "'" + value + "'";
    }

    @Override
    public String getOriginal() {
        return original;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
