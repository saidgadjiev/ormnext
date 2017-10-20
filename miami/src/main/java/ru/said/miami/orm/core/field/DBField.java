package ru.said.miami.orm.core.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {

    String columnName() default "";

    DataType dataType() default DataType.UNKNOWN;

    int length() default 255;

    boolean id() default false;

    boolean generated() default false;

    boolean foreign() default false;

    boolean foreignAutoCreate() default false;

    String get() default "";

    String set() default "";
}
