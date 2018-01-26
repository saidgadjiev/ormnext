package ru.said.orm.next.core.query.core.constraints.table;

import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.field.field_type.UniqueFieldType;
import ru.said.orm.next.core.query.visitor.QueryVisitor;

import java.util.List;
import java.util.stream.Collectors;

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
        visitor.start(this);
        visitor.finish(this);
    }
}
