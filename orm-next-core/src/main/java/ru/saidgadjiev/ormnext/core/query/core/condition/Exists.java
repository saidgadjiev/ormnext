package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.core.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent exists restriction.
 */
public class Exists implements Condition {

    /**
     * Checked operand.
     * @see Operand
     */
    private final Operand operand;

    /**
     * Exist sub query.
     * @see Select
     */
    private final Select select;

    /**
     * Create new exists.
     * @param select target sub query
     * @param operand target checked operand
     */
    public Exists(Select select, Operand operand) {
        this.select = select;
        this.operand = operand;
    }

    /**
     * Return current exist sub query.
     * @return select
     */
    public Select getSelect() {
        return select;
    }

    /**
     * Return current checked operand.
     * @return operand
     */
    public Operand getOperand() {
        return operand;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            operand.accept(visitor);
            select.accept(visitor);
        }
    }
}
