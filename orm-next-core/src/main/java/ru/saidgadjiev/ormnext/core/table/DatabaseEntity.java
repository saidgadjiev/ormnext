package ru.saidgadjiev.ormnext.core.table;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a class to be stored in the database. It is only required if you want to mark the class or
 * change its default tableName. You specify this annotation above the classes that you want to persist to the database.
 *
 * @author said gadjiev
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseEntity {

    /**
     * Table name. If not set then the name is taken from the class name lowercased.
     * @return table name
     */
    String name() default "";

    /**
     * Table indexes definition.
     * @return indexes definition
     */
    Index[] indexes() default {};

    /**
     * Table unique constraints definition.
     * @return unique constraints definition
     */
    Unique[] uniqueConstraints() default {};
}
