package ru.saidgadjiev.orm.next.core.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {

    String DEFAULT_STR = "__orm_next__ default string";

    String columnName() default "";

    int dataType() default DataType.UNKNOWN;

    int length() default 255;

    boolean notNull() default false;

    boolean id() default false;

    boolean generated() default false;

    boolean foreign() default false;

    boolean foreignAutoCreate() default false;

    String defaultValue() default DEFAULT_STR;

    String format() default "";
}
