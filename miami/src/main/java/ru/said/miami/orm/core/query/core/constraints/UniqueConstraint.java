package ru.said.miami.orm.core.query.core.constraints;

import ru.said.miami.orm.core.field.UniqueFieldType;
import ru.said.miami.orm.core.query.core.TableConstraint;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.List;

/**
 * Created by said on 28.10.17.
 */
public class UniqueConstraint implements TableConstraint {

    private UniqueFieldType uniqueFieldType;

    public UniqueConstraint(UniqueFieldType uniqueFieldType) {
        this.uniqueFieldType = uniqueFieldType;
    }

    public UniqueFieldType getUniqueFieldType() {
        return uniqueFieldType;
    }

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
