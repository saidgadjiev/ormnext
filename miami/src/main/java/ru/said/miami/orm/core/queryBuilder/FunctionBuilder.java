package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.query.core.Alias;
import ru.said.miami.orm.core.query.core.columnSpec.ColumnSpec;
import ru.said.miami.orm.core.query.core.function.CountAll;
import ru.said.miami.orm.core.query.core.function.Function;
import ru.said.miami.orm.core.query.core.function.CountExpression;
import ru.said.miami.orm.core.query.core.function.SUM;
import ru.said.miami.orm.core.table.TableInfo;

public class FunctionBuilder extends AbstractWhereBuilder {

    private final Alias alias;
    private TableInfo<?> tableInfo;

    FunctionBuilder(Alias alias, TableInfo<?> tableInfo) {
        this.alias = alias;
        this.tableInfo = tableInfo;
    }

    public FunctionBuilder column(String name) {
        ColumnSpec columnSpec = new ColumnSpec(name).alias(alias);

        super.operandCondition(columnSpec);

        return this;
    }

    public Function sum() {
        checkCurrentCondition();

        return new SUM(expression);
    }

    public Function count() {
        checkCurrentCondition();

        return new CountExpression(expression);
    }

    public Function countAll() {
        return new CountAll();
    }

    private ColumnSpec createColumnSpec(String name) {
        return new ColumnSpec(getColumnName(name)).alias(alias);
    }

    private String getColumnName(String fieldName) {
        return tableInfo.getDBFieldTypeByFieldName(fieldName)
                .orElseThrow(() ->  new IllegalArgumentException("Field[" + fieldName + "] does,t found"))
                .getColumnName();
    }
}
