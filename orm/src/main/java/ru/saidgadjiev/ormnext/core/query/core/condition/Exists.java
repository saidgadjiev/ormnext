package ru.saidgadjiev.ormnext.core.query.core.condition;

import ru.saidgadjiev.ormnext.core.query.core.Select;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

public class Exists implements Condition {

    private Select select;

    public Exists(Select select) {
        this.select = select;
    }

    public Select getSelect() {
        return select;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            select.accept(visitor);
        }
    }
}
