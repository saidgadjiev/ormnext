package ru.saidgadjiev.orm.next.core.criteria;

import ru.saidgadjiev.orm.next.core.query.core.Alias;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

/**
 * Created by said on 23.02.2018.
 */
public class CriteriaQueryVisitor extends NoActionVisitor {

    private final DatabaseEntityMetadata<?> databaseEntityMetadata;

    private final Alias alias;

    public CriteriaQueryVisitor(DatabaseEntityMetadata<?> databaseEntityMetadata, Alias alias) {
        this.databaseEntityMetadata = databaseEntityMetadata;
        this.alias = alias;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        if (columnSpec.getAlias() == null) {
            columnSpec.alias(alias).name(databaseEntityMetadata.getPersistenceName(columnSpec.getName()));
        }

        return false;
    }

    @Override
    public boolean visit(TableRef tableRef) {
        if (tableRef.getAlias() == null) {
            tableRef.alias(alias);
        }

        return false;
    }

}
