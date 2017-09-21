package ru.said.miami.orm.core.cache.core.clause.element;

/**
 * Created by said on 17.06.17.
 */
public class LongLiteral extends Literal<Long> {
    private Long value;

    public LongLiteral(Long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }
}
