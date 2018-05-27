package ru.saidgadjiev.ormnext.core.field;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for provide getter method for field.
 * By default orm next thinks that the class is java bean, if you provide getter method
 * orm next use your provided method and don't try find java bean getter method.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Getter {

    /**
     * Getter name.
     * @return getter name
     */
    String name();
}
