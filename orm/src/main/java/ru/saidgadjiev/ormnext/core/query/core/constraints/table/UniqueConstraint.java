package ru.saidgadjiev.ormnext.core.query.core.constraints.table;

import ru.saidgadjiev.ormnext.core.field.fieldtype.UniqueFieldType;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.List;

/**
 * Created by said on 28.10.17.
 */
public class UniqueConstraint implements TableConstraint {

    private UniqueFieldType uniqueFieldType;

    public UniqueConstraint(UniqueFieldType uniqueFieldType) {
        this.uniqueFieldType = uniqueFieldType;
    }

    public List<String> getUniqueColemns() {
        return uniqueFieldType.getDbFieldTypes();
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
