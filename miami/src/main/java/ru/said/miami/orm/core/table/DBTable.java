package ru.said.miami.orm.core.table;


import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.field.PrimaryKey;

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

    PrimaryKey pk();
}
