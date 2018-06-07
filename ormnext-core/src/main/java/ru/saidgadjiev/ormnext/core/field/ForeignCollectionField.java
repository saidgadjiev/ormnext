package ru.saidgadjiev.ormnext.core.field;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Foreign collection field annotation. Use for Many to One relation.
 *
 * @author Said Gadjiev
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignCollectionField {

    /**
     * Foreign field name. If it not defined try find field via reflection.
     *
     * @return foreign field name
     */
    String foreignFieldName() default "";

    /**
     * Collection fetch type. Default is {@link FetchType#EAGER}
     *
     * @return fetch type
     */
    FetchType fetchType() default FetchType.EAGER;

    /**
     * Is foreign auto create?
     *
     * @return true if foreign auto create
     */
    boolean foreignAutoCreate() default false;
}
