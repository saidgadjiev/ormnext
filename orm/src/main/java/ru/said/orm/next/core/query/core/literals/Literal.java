package ru.said.orm.next.core.query.core.literals;

import ru.said.orm.next.core.query.core.Operand;

/**
 * Created by said on 21.01.2018.
 */
public interface Literal<T> extends RValue, Operand {

    String getOriginal();

    T get();
}
