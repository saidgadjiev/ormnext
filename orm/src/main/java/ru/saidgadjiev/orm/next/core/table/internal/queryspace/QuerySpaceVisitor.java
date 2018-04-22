package ru.saidgadjiev.orm.next.core.table.internal.queryspace;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.orm.next.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;

public class QuerySpaceVisitor extends NoActionVisitor {

    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    private EntityAliases entityAliases;

    public QuerySpaceVisitor(DatabaseEntityMetadata<?> databaseEntityMetadata, EntityAliases entityAliases) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.entityAliases = entityAliases;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        columnSpec
                .name(databaseEntityMetadata.getColumnNameByPropertyName(columnSpec.getName()))
                .alias(new Alias(entityAliases.getTableAlias()));

        return false;
    }
}
