package ru.saidgadjiev.ormnext.core.criteria.impl;

import java.util.Arrays;
import java.util.List;

/**
 * This class use for return restriction argument info.
 */
public class CriterionArgument {

    /**
     * Property name.
     */
    private final String property;

    /**
     * Args values.
     */
    private final Object[] values;

    /**
     * Create new instance.
     * @param property target property name
     * @param values args values
     */
    CriterionArgument(String property, Object... values) {
        this.property = property;
        this.values = values;
    }

    /**
     * Return current property name.
     * @return current property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * Return args.
     * @return args.
     */
    public List<Object> getValues() {
        return Arrays.asList(values);
    }
}
