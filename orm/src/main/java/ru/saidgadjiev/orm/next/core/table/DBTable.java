package ru.saidgadjiev.orm.next.core.table;


import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {

    String name() default "";

    Class<? extends BaseSessionManagerImpl> daoClass() default BaseSessionManagerImpl.class;

    Index[] indexes() default {};

    Unique[] uniqueConstraints() default {};
}
