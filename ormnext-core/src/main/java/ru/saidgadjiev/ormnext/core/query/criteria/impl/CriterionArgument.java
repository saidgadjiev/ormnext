package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import java.util.Arrays;
import java.util.List;

/**
 * This class use for return restriction argument info.
 *
 * @author Said Gadjiev
 */
public class CriterionArgument {

    /**
     * Hashcode magic number.
     */
    private static final int HASHCODE_MAGIC_NUMBER = 31;

    /**
     * Property name.
     */
    private final String property;

    /**
     * Args values.
     */
    private final Object[] values;

    /**
     * Create a new instance.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CriterionArgument that = (CriterionArgument) o;

        if (property != null ? !property.equals(that.property) : that.property != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = HASHCODE_MAGIC_NUMBER * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "CriterionArgument{"
                + "property='" + property + '\''
                + ", values=" + Arrays.toString(values)
                + '}';
    }
}
