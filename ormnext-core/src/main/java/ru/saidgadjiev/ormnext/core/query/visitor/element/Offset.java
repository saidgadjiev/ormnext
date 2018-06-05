package ru.saidgadjiev.ormnext.core.query.visitor.element;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * This class represent offset part sql.
 *
 * @author said gadjiev
 */
public class Offset implements QueryElement {

    /**
     * Offset.
     */
    private final int offset;

    /**
     * Create a new instance.
     * @param offset target offset value
     */
    public Offset(int offset) {
        this.offset = offset;
    }

    /**
     * Return current offset.
     * @return current offset
     */
    public int getOffset() {
        return offset;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
