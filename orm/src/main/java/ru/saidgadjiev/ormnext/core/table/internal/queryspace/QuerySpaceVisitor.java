package ru.saidgadjiev.ormnext.core.table.internal.queryspace;

import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

public class QuerySpaceVisitor extends NoActionVisitor {

    private DatabaseEntityMetadata<?> databaseEntityMetadata;

    private EntityAliases entityAliases;

    public QuerySpaceVisitor(DatabaseEntityMetadata<?> databaseEntityMetadata, EntityAliases entityAliases) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.entityAliases = entityAliases;
    }

    @Override
    public boolean visit(TableRef tableRef) {
        if (tableRef.getAlias() == null) {
            tableRef
                    .alias(new Alias(entityAliases.getTableAlias()));
        }

        return false;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        if (columnSpec.getAlias() == null) {
            columnSpec
                    .name(databaseEntityMetadata.getColumnNameByPropertyName(columnSpec.getName()))
                    .alias(new Alias(entityAliases.getTableAlias()));
        }

        return false;
    }
}
