package ru.saidgadjiev.ormnext.core.criteria;

import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.visitor.NoActionVisitor;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.query.core.Alias;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

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
            columnSpec.alias(alias).name(databaseEntityMetadata.getColumnNameByPropertyName(columnSpec.getName()));
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
