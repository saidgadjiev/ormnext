package ru.saidgadjiev.ormnext.core.field;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation for One to One or One to Many relation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignColumn {

    /**
     * True if need auto create foreign object.
     * @return foreign object auto create.
     */
    boolean foreignAutoCreate() default false;

    /**
     * Column name. Defaults to the property or field name.
     * @return column name.
     */
    String columnName() default "";

    /**
     * Fetch type. Default is {@link FetchType#EAGER}
     * @return fetch type
     */
    FetchType fetchType() default FetchType.EAGER;
}
