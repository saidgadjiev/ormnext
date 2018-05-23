package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.core.Operand;

/**
 * This is the abstract class for implement literal.
 * @param <T> literal type
 */
public interface Literal<T> extends RValue, Operand {

    /**
     * Get sql literal present.
     * @return sql present for current literal
     */
    String getOriginal();

    /**
     * Get current literal.
     * @return current literal.
     */
    T get();
}
