package ru.saidgadjiev.ormnext.core.field;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Converter groups.
 *
 * @author Said Gadjiev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConverterGroup {

    /**
     * Converters.
     * @return converters
     * @see Converter
     */
    Converter[] converters();
}
