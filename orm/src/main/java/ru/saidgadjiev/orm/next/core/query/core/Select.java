package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.query.core.clause.GroupBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.Having;
import ru.saidgadjiev.orm.next.core.query.core.clause.OrderBy;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromExpression;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsStrategy;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumn;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedColumnSpec;
import ru.saidgadjiev.orm.next.core.query.core.common.TableRef;
import ru.saidgadjiev.orm.next.core.query.core.condition.Equals;
import ru.saidgadjiev.orm.next.core.query.core.condition.Expression;
import ru.saidgadjiev.orm.next.core.query.core.join.JoinExpression;
import ru.saidgadjiev.orm.next.core.query.core.join.LeftJoin;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

/**
 * Класс SELECT запроса
 */
public class Select implements QueryElement {

    private FromExpression from;

    private SelectionMode selectionMode;

    private SelectColumnsStrategy selectColumnsStrategy;

    private Expression where = new Expression();

    private OrderBy orderBy;

    private GroupBy groupBy;

    private Having having;

    private Limit limit;

    private Offset offset;

    public FromExpression getFrom() {
        return from;
    }

    public void setFrom(FromExpression from) {
        this.from = from;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    public SelectColumnsStrategy getSelectColumnsStrategy() {
        return selectColumnsStrategy;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public void setSelectColumnsStrategy(SelectColumnsStrategy selectColumnsStrategy) {
        this.selectColumnsStrategy = selectColumnsStrategy;
    }

    public Having getHaving() {
        return having;
    }

    public void setHaving(Having having) {
        this.having = having;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    public static <ID> Select buildQueryById(TableInfo<?> tableInfo, ID id) {
        Select selectQuery = new Select();

        selectQuery.setSelectColumnsStrategy(selectColumnsStrategy(tableInfo, new SelectColumnsList()));
        selectQuery.setFrom(from(tableInfo));
        AndCondition andCondition = new AndCondition();
        IDBFieldType idField = tableInfo.getPrimaryKey().get();

        andCondition.add(new Equals(new ColumnSpec(idField.getColumnName()).alias(new Alias(tableInfo.getTableName())), idField.getDataPersister().getLiteral(idField, id)));
        selectQuery.getWhere().getConditions().add(andCondition);

        return selectQuery;
    }

    private static FromExpression from(TableInfo<?> tableInfo) {
        if (tableInfo.toForeignFieldTypes().isEmpty()) {
            return new FromTable(new TableRef(tableInfo.getTableName()).alias(new Alias(tableInfo.getTableName())));
        }
        FromJoinedTables fromJoinedTables = new FromJoinedTables(new TableRef(tableInfo.getTableName()).alias(new Alias(tableInfo.getTableName())));

        appendJoinExpression(fromJoinedTables, tableInfo);

        return fromJoinedTables;
    }

    private static void appendJoinExpression(FromJoinedTables from, TableInfo<?> tableInfo) {
        for (ForeignFieldType foreignFieldType: tableInfo.toForeignFieldTypes()) {
            appendJoinExpression(from, TableInfoManager.buildOrGet(foreignFieldType.getForeignFieldClass()));
            Expression onExpression = new Expression();
            AndCondition andCondition = new AndCondition();

            andCondition.add(
                    new Equals(
                            new ColumnSpec(foreignFieldType.getColumnName()).alias(new Alias(tableInfo.getTableName())),
                            new ColumnSpec(foreignFieldType.getForeignPrimaryKey().getColumnName()).alias(new Alias(foreignFieldType.getForeignTableName()))
                    )
            );
            onExpression.add(andCondition);

            JoinExpression joinExpression = new LeftJoin(new TableRef(foreignFieldType.getForeignTableName()).alias(new Alias(foreignFieldType.getForeignTableName())), onExpression);

            from.add(joinExpression);
        }
    }

    private static SelectColumnsStrategy selectColumnsStrategy(TableInfo<?> tableInfo, SelectColumnsList selectColumnsList) {
        for (IDBFieldType idbFieldType: tableInfo.getFieldTypes()) {
            if (idbFieldType.isForeignCollectionFieldType()) {
                continue;
            }
            ColumnSpec columnSpec = new ColumnSpec(idbFieldType.getColumnName()).alias(new Alias(tableInfo.getTableName()));
            DisplayedColumnSpec displayedColumnSpec = new DisplayedColumn(columnSpec);

            displayedColumnSpec.setAlias(new Alias(tableInfo.getTableName() + "_" + idbFieldType.getColumnName()));
            selectColumnsList.addColumn(displayedColumnSpec);
        }
        for (ForeignFieldType foreignFieldType: tableInfo.toForeignFieldTypes()) {
            selectColumnsStrategy(TableInfoManager.buildOrGet(foreignFieldType.getForeignFieldClass()), selectColumnsList);
        }

        return selectColumnsList;
    }

    public static Select buildQueryForAll(String typeName) {
        Select selectQuery = new Select();

        selectQuery.setFrom(new FromTable(new TableRef(typeName)));
        selectQuery.setSelectColumnsStrategy(new SelectAll());

        return selectQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            selectColumnsStrategy.accept(visitor);
            from.accept(visitor);
            if (where != null) {
                where.accept(visitor);
            }
            if (groupBy != null) {
                groupBy.accept(visitor);
            }
            if (orderBy != null) {
                orderBy.accept(visitor);
            }
            if (having != null) {
                having.accept(visitor);
            }
            if (limit != null) {
                limit.accept(visitor);
            }
            if (offset != null) {
                offset.accept(visitor);
            }
        }
    }

    public Limit getLimit() {
        return limit;
    }

    public Offset getOffset() {
        return offset;
    }

    enum SelectionMode {
        DISTINCT
    }

}
