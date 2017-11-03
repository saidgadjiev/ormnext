package ru.said.miami.orm.core.table;


import ru.said.miami.orm.core.dao.BaseDaoImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {

    String name() default "";

    Class<? extends BaseDaoImpl> daoClass() default BaseDaoImpl.class;

    Index[] indexes() default {};

    Unique[] uniqueConstraints() default {};
}
