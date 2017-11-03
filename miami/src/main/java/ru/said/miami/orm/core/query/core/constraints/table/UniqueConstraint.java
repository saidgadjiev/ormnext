package ru.said.miami.orm.core.query.core.constraints.table;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.field.UniqueFieldType;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.List;
import java.util.function.Function;
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
        return uniqueFieldType.getDbFieldTypes().stream().map(DBFieldType::getColumnName).collect(Collectors.toList());
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
