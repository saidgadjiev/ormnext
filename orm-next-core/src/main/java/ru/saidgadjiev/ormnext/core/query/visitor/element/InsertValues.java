package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.RValue;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent insert values.
 */
public class InsertValues implements QueryElement {

    /**
     * Values.
     * @see RValue
     */
    private final List<RValue> values = new ArrayList<>();


    /**
     * Add new value.
     * @param value target value
     * @see RValue
     */
    public void add(RValue value) {
        values.add(value);
    }

    /**
     * Add all new values.
     * @param values target values
     * @see RValue
     */
    public void addAll(List<RValue> values) {
        this.values.addAll(values);
    }

    /**
     * Return current insert values.
     * @return current insert values
     */
    public List<RValue> getValues() {
        return values;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
