package ru.saidgadjiev.ormnext.core.table;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for define table index.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    /**
     * Index name.
     * @return index name
     */
    String name();

    /**
     * Indexed property names.
     * @return indexed property names
     */
    String[] columns();

    /**
     * Is unique index. Default it is false.
     * @return is unique index
     */
    boolean unique() default false;
}
