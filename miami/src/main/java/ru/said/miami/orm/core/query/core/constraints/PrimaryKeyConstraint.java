package ru.said.miami.orm.core.query.core.constraints;

import ru.said.miami.orm.core.field.PrimaryKeyFieldType;
import ru.said.miami.orm.core.query.core.TableConstraint;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

/**
 * Created by said on 28.10.17.
 */
public class PrimaryKeyConstraint implements TableConstraint {

    private PrimaryKeyFieldType primaryKeyFieldType;

    public PrimaryKeyConstraint(PrimaryKeyFieldType primaryKeyFieldType) {
        this.primaryKeyFieldType = primaryKeyFieldType;
    }

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
