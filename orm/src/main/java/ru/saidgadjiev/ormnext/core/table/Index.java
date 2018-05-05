package ru.saidgadjiev.ormnext.core.table;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name();

    String[] columns();

    boolean unique() default false;
}
