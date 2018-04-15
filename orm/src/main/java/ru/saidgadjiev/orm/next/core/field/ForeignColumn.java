package ru.saidgadjiev.orm.next.core.field;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignColumn {

    boolean foreignAutoCreate() default false;

    String columnName() default "";
}
