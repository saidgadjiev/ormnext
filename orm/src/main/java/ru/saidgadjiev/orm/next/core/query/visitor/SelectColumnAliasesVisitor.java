package ru.saidgadjiev.orm.next.core.query.visitor;

import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumn;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 08.04.2018.
 */
public class SelectColumnAliasesVisitor extends NoActionVisitor {

    private Map<String, > columnAliasMap = new HashMap<>();

    public SelectColumnAliasesVisitor() {
    }

    @Override
    public void visit(DisplayedColumn displayedColumn) {
        super.visit(displayedColumn);
    }

    @Override
    public void visit(DisplayedOperand displayedOperand) {
        super.visit(displayedOperand);
    }
}
