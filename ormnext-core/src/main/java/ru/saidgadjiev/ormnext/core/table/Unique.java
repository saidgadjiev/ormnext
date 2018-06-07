package ru.saidgadjiev.ormnext.core.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for define unique constraint.
 *
 * @author Said Gadjiev
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

    /**
     * Unique property names.
     * @return unique property names
     */
    String[] columns();
}
