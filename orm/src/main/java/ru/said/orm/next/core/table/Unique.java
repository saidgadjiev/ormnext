package ru.said.orm.next.core.table;

//TODO: загуглить как выглядят поля unique в sqlite

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

    String[] columns();
}
