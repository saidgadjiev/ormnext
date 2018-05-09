package ru.saidgadjiev.ormnext.core.query.core.columnspec;


import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.core.Operand;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class ColumnSpec implements Operand {

    private String name;

    private Alias alias;

    public ColumnSpec(String name) {
        this.name = name;
    }

    public ColumnSpec alias(Alias alias) {
        this.alias = alias;

        return this;
    }

    public Alias getAlias() {
        return alias;
    }

    public String getName() {
        return name;
    }

    public ColumnSpec name(String name) {
        this.name = name;

        return this;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            alias.accept(visitor);
        }
    }

}
