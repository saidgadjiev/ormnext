package ru.saidgadjiev.ormnext.core.field;

import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;

import java.lang.annotation.*;

/**
 * Field value converter.
 *
 * @author Said Gadjiev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Converter.Converters.class)
public @interface Converter {

    /**
     * Converter class.
     *
     * @return converter class
     */
    Class<? extends ColumnConverter> value();

    /**
     * Converter constructor args.
     *
     * @return converter constructor args
     */
    String[] args() default {};

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Converters {
        Converter[] value();
    }
}
