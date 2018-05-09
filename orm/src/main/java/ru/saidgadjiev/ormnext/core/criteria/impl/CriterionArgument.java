package ru.saidgadjiev.ormnext.core.criteria.impl;

public class CriterionArgument {

    private final String propertyName;

    private final Object[] values;

    public CriterionArgument(String propertyName, Object ... values) {
        this.propertyName = propertyName;
        this.values = values;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object[] getValues() {
        return values;
    }
}
