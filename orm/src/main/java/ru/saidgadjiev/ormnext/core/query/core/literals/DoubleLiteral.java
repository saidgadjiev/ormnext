package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 03.02.2018.
 */
public class DoubleLiteral implements Literal<Double> {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public String getOriginal() {
        return String.valueOf(value);
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
