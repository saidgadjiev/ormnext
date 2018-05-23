package ru.saidgadjiev.ormnext.core.query.core.constraints.table;

import ru.saidgadjiev.ormnext.core.field.fieldtype.UniqueFieldType;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.List;

/**
 * Unique table constraint.
 */
public class UniqueConstraint implements TableConstraint {

    /**
     * Unique column type.
     * @see UniqueFieldType
     */
    private UniqueFieldType uniqueFieldType;

    /**
     * Create new instance.
     * @param uniqueFieldType target unique column type
     */
    public UniqueConstraint(UniqueFieldType uniqueFieldType) {
        this.uniqueFieldType = uniqueFieldType;
    }

    /**
     * Return current unique column names from {@link UniqueFieldType#getColumnNames()}.
     * @return {@link UniqueConstraint#uniqueFieldType#getColumnNames()}
     */
    public List<String> getUniqueColumnNames() {
        return uniqueFieldType.getColumnNames();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
