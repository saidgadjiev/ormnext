package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field value converter.
 *
 * @author Said Gadjiev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Converter {

    /**
     * Converter class.
     * @return converter class
     */
    Class<? extends ColumnConverter> value();

    /**
     * Converter constructor args.
     * @return converter constructor args
     */
    String[] args() default {};

}
