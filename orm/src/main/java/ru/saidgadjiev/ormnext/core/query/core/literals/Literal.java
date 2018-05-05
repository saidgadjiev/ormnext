package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.Operand;

/**
 * Created by said on 21.01.2018.
 */
public interface Literal<T> extends RValue, Operand {

    String getOriginal();

    T get();
}
