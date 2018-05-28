package ru.saidgadjiev.ormnext.core.query_element;

import ru.saidgadjiev.ormnext.core.loader.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * This class represent drop index query.
 */
public class DropIndexQuery implements QueryElement {

    /**
     * Index name.
     */
    private String name;

    /**
     * Create a new instance.
     * @param name target index name
     */
    public DropIndexQuery(String name) {
        this.name = name;
    }

    /**
     * Return current index name.
     * @return current index name
     */
    public String getName() {
        return name;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

}
