package ru.saidgadjiev.ormnext.core.query.core.constraints.attribute;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Attribute default constraint. It will be represented like this DEFAULT {@link Default#defaultDefinition}.
 */
public class Default implements AttributeConstraint {

    /**
     * Default definition.
     */
    private String defaultDefinition;

    /**
     * Create new instance.
     * @param defaultDefinition target default definition
     */
    public Default(String defaultDefinition) {
        this.defaultDefinition = defaultDefinition;
    }

    /**
     * Return current default definition.
     * @return defaultDefinition
     */
    public String getDefaultDefinition() {
        return defaultDefinition;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
