package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent UPDATE query.
 */
public class UpdateQuery implements QueryElement {

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Update values list.
     * @see UpdateValue
     */
    private final List<UpdateValue> updateValues = new ArrayList<>();

    /**
     * Where expression.
     * @see Expression
     */
    private Expression where = new Expression();

    public UpdateQuery(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Add new update value.
     * @param updateValue target update value
     */
    public void add(UpdateValue updateValue) {
        updateValues.add(updateValue);
    }

    /**
     * Add new update values list.
     * @param values target update values
     */
    public void addAll(List<UpdateValue> values) {
        updateValues.addAll(values);
    }

    /**
     * Return current update values.
     * @return current update values
     */
    public List<UpdateValue> getUpdateValues() {
        return updateValues;
    }

    /**
     * Return current table name.
     * @return current table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return current where expression.
     * @return current where expression
     */
    public Expression getWhere() {
        return where;
    }

    /**
     * Provide where expression.
     * @param where target expression
     */
    public void setWhere(Expression where) {
        this.where = where;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            updateValues.forEach(updateValue -> updateValue.accept(visitor));
            where.accept(visitor);
        }
    }
}
