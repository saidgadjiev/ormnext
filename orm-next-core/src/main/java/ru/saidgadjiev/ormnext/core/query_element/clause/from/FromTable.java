package ru.saidgadjiev.ormnext.core.query_element.clause.from;

import ru.saidgadjiev.ormnext.core.query_element.common.TableRef;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

/**
 * This class represent from clause from single table.
 */
public class FromTable implements FromExpression {

    /**
     * From table ref.
     * @see TableRef
     */
    private TableRef tableRef;

    /**
     * Create new instance with provided table.
     * @param tableRef target table
     */
    public FromTable(TableRef tableRef) {
        this.tableRef = tableRef;
    }

    /**
     * Return current from table ref.
     * @return tableRef
     */
    public TableRef getTableRef() {
        return tableRef;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            tableRef.accept(visitor);
        }
    }

}
