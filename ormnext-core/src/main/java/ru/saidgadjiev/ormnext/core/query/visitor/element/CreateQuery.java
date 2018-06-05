package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.UpdateValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent INSERT query.
 *
 * @author said gadjiev
 */
public class CreateQuery implements SqlStatement {

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * UpdateValue values.
     *
     * @see UpdateValue
     */
    private final List<UpdateValue> updateValues = new ArrayList<>();

    /**
     * Create a new instance.
     *
     * @param tableName target table name
     */
    public CreateQuery(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Add a new update values.
     *
     * @param updateValue target insert values
     */
    public void add(UpdateValue updateValue) {
        this.updateValues.add(updateValue);
    }

    /**
     * Return current update values.
     *
     * @return current update values
     */
    public List<UpdateValue> getUpdateValues() {
        return updateValues;
    }

    /**
     * Return current insert table name.
     *
     * @return current insert table name
     */
    public String getTableName() {
        return tableName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            updateValues.forEach(updateValue -> updateValue.accept(visitor));
        }
    }
}
