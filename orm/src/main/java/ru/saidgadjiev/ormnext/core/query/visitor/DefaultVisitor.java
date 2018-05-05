package ru.saidgadjiev.ormnext.core.query.visitor;

import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query.core.clause.*;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.ormnext.core.query.core.clause.select.SelectAll;
import ru.saidgadjiev.ormnext.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.core.condition.*;
import ru.saidgadjiev.ormnext.core.query.core.constraints.attribute.*;
import ru.saidgadjiev.ormnext.core.query.core.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.ormnext.core.query.core.constraints.table.TableConstraint;
import ru.saidgadjiev.ormnext.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.ormnext.core.query.core.function.CountAll;
import ru.saidgadjiev.ormnext.core.query.core.function.CountColumn;
import ru.saidgadjiev.ormnext.core.query.core.function.CountExpression;
import ru.saidgadjiev.ormnext.core.query.core.function.SUM;
import ru.saidgadjiev.ormnext.core.query.core.join.JoinExpression;
import ru.saidgadjiev.ormnext.core.query.core.join.JoinInfo;
import ru.saidgadjiev.ormnext.core.query.core.join.LeftJoin;
import ru.saidgadjiev.ormnext.core.query.core.literals.*;
import ru.saidgadjiev.ormnext.core.db.DatabaseType;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query.core.clause.*;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromJoinedTables;
import ru.saidgadjiev.ormnext.core.query.core.clause.from.FromTable;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.core.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.core.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.core.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.core.condition.Equals;
import ru.saidgadjiev.ormnext.core.query.core.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.core.condition.InSelect;
import ru.saidgadjiev.ormnext.core.query.core.constraints.attribute.*;
import ru.saidgadjiev.ormnext.core.query.core.constraints.table.UniqueConstraint;
import ru.saidgadjiev.ormnext.core.query.core.join.JoinExpression;
import ru.saidgadjiev.ormnext.core.query.core.join.JoinInfo;

import java.util.Iterator;

/**
 * Visitor по умолчанию
 */
public class DefaultVisitor extends NoActionVisitor {

    protected StringBuilder sql = new StringBuilder();

    private DatabaseType databaseType;

    private final String escapeEntity;

    private final String escapeLiteral;

    public DefaultVisitor(DatabaseType databaseType) {
        this.databaseType = databaseType;
        escapeEntity = databaseType.getEntityNameEscape();
        escapeLiteral = databaseType.getValueEscape();
    }

    public String getQuery() {
        return sql.toString();
    }

    @Override
    public boolean visit(CreateQuery tCreateQuery) {
        sql.append("INSERT INTO ").append(escapeEntity).append(tCreateQuery.getTypeName()).append(escapeEntity);

        if (tCreateQuery.getColumnNames().isEmpty()) {
            sql.append(" ").append(databaseType.appendNoColumn());
        } else {
            sql.append(" (");

            for (Iterator<String> iterator = tCreateQuery.getColumnNames().iterator(); iterator.hasNext(); ) {
                sql.append(escapeEntity).append(iterator.next()).append(escapeEntity);
                if (iterator.hasNext()) {
                    sql.append(",");
                }
            }
            sql.append(")");
            sql.append(" VALUES (");
            for (Iterator<InsertValues> iterator = tCreateQuery.getInsertValues().iterator(); iterator.hasNext(); ) {
                InsertValues insertValues = iterator.next();

                insertValues.accept(this);
                if (iterator.hasNext()) {
                    sql.append(", ");
                }
            }
            sql.append(")");
        }
        
        return false;
    }

    @Override
    public void visit(StringLiteral stringLiteral) {
        if (stringLiteral.getOriginal() == null) {
            sql.append("null");
        } else {
            sql.append(escapeLiteral).append(stringLiteral.getOriginal()).append(escapeLiteral);
        }
    }

    @Override
    public boolean visit(Select tSelectQuery) {
        sql.append("SELECT ");
        if (tSelectQuery.getSelectColumnsStrategy() != null) {
            tSelectQuery.getSelectColumnsStrategy().accept(this);
        }
        sql.append(" FROM ");
        if (tSelectQuery.getFrom() != null) {
            tSelectQuery.getFrom().accept(this);
        }

        if (tSelectQuery.getWhere() != null) {
            if (!tSelectQuery.getWhere().getConditions().isEmpty()) {
                sql.append(" WHERE ");
            }
            tSelectQuery.getWhere().accept(this);
        }
        if (tSelectQuery.getGroupBy() != null) {
            tSelectQuery.getGroupBy().accept(this);
        }
        if (tSelectQuery.getOrderBy() != null) {
            tSelectQuery.getOrderBy().accept(this);
        }
        if (tSelectQuery.getHaving() != null) {
            tSelectQuery.getHaving().accept(this);
        }
        if (tSelectQuery.getLimit() != null) {
            tSelectQuery.getLimit().accept(this);
        }
        if (tSelectQuery.getOffset() != null) {
            tSelectQuery.getOffset().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(Expression expression) {
        for (Iterator<AndCondition> iterator = expression.getConditions().iterator(); iterator.hasNext(); ) {
            AndCondition andCondition = iterator.next();

            sql.append("(");

            for (Iterator<Condition> iterator1 = andCondition.getConditions().iterator(); iterator1.hasNext(); ) {
                Condition condition = iterator1.next();

                sql.append("(");

                condition.accept(this);

                sql.append(")");

                if (iterator1.hasNext()) {
                    sql.append(" AND ");
                }
            }

            sql.append(")");

            if (iterator.hasNext()) {
                sql.append(" OR ");
            }
        }

        return false;
    }

    @Override
    public boolean visit(Equals equals) {
        equals.getFirst().accept(this);
        sql.append(" = ");
        equals.getSecond().accept(this);

        return false;
    }

    @Override
    public boolean visit(ColumnSpec columnSpec) {
        if (columnSpec.getAlias() != null) {
            columnSpec.getAlias().accept(this);
            sql.append(".");
        }
        sql.append(escapeEntity).append(columnSpec.getName()).append(escapeEntity);

        return false;
    }

    @Override
    public boolean visit(TableRef tableRef) {
        sql.append(escapeEntity).append(tableRef.getTableName()).append(escapeEntity);

        if (tableRef.getAlias() != null) {
            sql.append(" AS ");
            tableRef.getAlias().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(CreateTableQuery tCreateTableQuery) {
        sql.append("CREATE TABLE ");
        if (tCreateTableQuery.isIfNotExists()) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append(escapeEntity).append(tCreateTableQuery.getTypeName()).append(escapeEntity).append(" (");
        for (Iterator<AttributeDefinition> iterator = tCreateTableQuery.getAttributeDefinitions().iterator(); iterator.hasNext(); ) {
            AttributeDefinition attributeDefinition = iterator.next();

            sql.append(escapeEntity).append(attributeDefinition.getName()).append(escapeEntity).append(" ");
            sql.append(databaseType.getTypeSqlPresent(attributeDefinition));
            sql.append(" ");
            for (Iterator<AttributeConstraint> constraintIterator = attributeDefinition.getAttributeConstraints().iterator(); constraintIterator.hasNext(); ) {
                AttributeConstraint constraint = constraintIterator.next();

                constraint.accept(this);

                if (constraintIterator.hasNext()) {
                    sql.append(" ");
                }
            }
            if (iterator.hasNext()) {
                sql.append(", ");
            }
        }
        if (!tCreateTableQuery.getTableConstraints().isEmpty()) {
            sql.append(", ");
            for (Iterator<TableConstraint> iterator = tCreateTableQuery.getTableConstraints().iterator(); iterator.hasNext(); ) {
                TableConstraint tableConstraint = iterator.next();

                tableConstraint.accept(this);
                if (iterator.hasNext()) {
                    sql.append(", ");
                }
            }
        }
        sql.append(")");

        return false;
    }

    @Override
    public boolean visit(DeleteQuery deleteQuery) {
        sql.append("DELETE FROM ").append(deleteQuery.getTypeName());
        if (!deleteQuery.getWhere().getConditions().isEmpty()) {
            sql.append(" WHERE ");
        }

        return true;
    }

    @Override
    public void visit(IntLiteral intLiteral) {
        sql.append(intLiteral.getOriginal());
    }

    @Override
    public boolean visit(UpdateQuery updateQuery) {
        sql.append("UPDATE ").append(updateQuery.getTypeName()).append(" SET ");

        for (Iterator<UpdateValue> iterator = updateQuery.getUpdateValues().iterator(); iterator.hasNext(); ) {
            UpdateValue updateValue = iterator.next();
            RValue value = updateValue.getValue();

            sql.append(updateValue.getName()).append("=");
            value.accept(this);
            if (iterator.hasNext()) {
                sql.append(",");
            }
        }
        if (!updateQuery.getWhere().getConditions().isEmpty()) {
            sql.append(" WHERE ");
            updateQuery.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public void visit(DropTableQuery dropTableQuery) {
        sql.append("DROP TABLE ");
        if (dropTableQuery.isIfExists()) {
            sql.append("IF EXISTS ");
        }
        sql.append(escapeEntity).append(dropTableQuery.getTableName()).append(escapeEntity);
    }

    @Override
    public void visit(PrimaryKeyConstraint primaryKeyConstraint) {
        sql.append(databaseType.appendPrimaryKey(primaryKeyConstraint.isGenerated()));
    }

    @Override
    public void visit(UniqueConstraint uniqueConstraint) {
        sql.append("UNIQUE (").append(escapeEntity);
        for (Iterator<String> iterator = uniqueConstraint.getUniqueColemns().iterator(); iterator.hasNext(); ) {
            sql.append(iterator.next());
            if (iterator.hasNext()) {
                sql.append(escapeEntity).append(",").append(escapeEntity);
            }
        }
        sql.append(escapeEntity).append(")");
    }

    @Override
    public void visit(NotNullConstraint notNullConstraint) {
        sql.append("NOT NULL");
    }

    @Override
    public void visit(ReferencesConstraint referencesConstraint) {
        sql.append(" REFERENCES ").append(escapeEntity)
                .append(referencesConstraint.getTypeName())
                .append(escapeEntity)
                .append("(")
                .append(escapeEntity)
                .append(referencesConstraint.getColumnName())
                .append(escapeEntity)
                .append(")");
    }

    @Override
    public void visit(CreateIndexQuery createIndexQuery) {
        sql.append("CREATE ")
                .append(createIndexQuery.isUnique() ? "UNIQUE" : "INDEX")
                .append(" ")
                .append(escapeEntity)
                .append(createIndexQuery.getIndexName())
                .append(escapeEntity)
                .append(" ON ")
                .append(escapeEntity)
                .append(createIndexQuery.getTableName())
                .append(escapeEntity)
                .append("(")
                .append(escapeEntity);
        for (Iterator<String> iterator = createIndexQuery.getColumns().iterator(); iterator.hasNext(); ) {
            sql.append(iterator.next());

            if (iterator.hasNext()) {
                sql.append(escapeEntity).append(",").append(escapeEntity);
            }
        }
        sql.append(escapeEntity).append(")");
    }

    @Override
    public void visit(DropIndexQuery dropIndexQuery) {
        sql.append(escapeEntity).append("DROP INDEX ").append(dropIndexQuery.getName()).append(escapeEntity);
    }

    @Override
    public void visit(Param param) {
        sql.append("?");
    }

    @Override
    public void visit(SelectAll selectAll) {
        sql.append("*");
    }

    @Override
    public boolean visit(SelectColumnsList selectColumnsList) {
        for (Iterator<DisplayedColumnSpec> columnIterator = selectColumnsList.getColumns().iterator(); columnIterator.hasNext(); ) {
            DisplayedColumnSpec columnSpec = columnIterator.next();

            columnSpec.accept(this);
            if (columnIterator.hasNext()) {
                sql.append(", ");
            }
        }

        return false;
    }

    @Override
    public boolean visit(Having having) {
        sql.append(" HAVING ");
        if (having.getExpression() != null) {
            having.getExpression().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(GroupBy groupBy) {
        sql.append(" GROUP BY ");

        for (Iterator<GroupByItem> columnSpecIterator = groupBy.getGroupByItems().iterator(); columnSpecIterator.hasNext(); ) {
            GroupByItem columnSpec = columnSpecIterator.next();

            columnSpec.accept(this);
            if (columnSpecIterator.hasNext()) {
                sql.append(",");
            }
        }

        return false;
    }

    @Override
    public boolean visit(FromTable fromTable) {
        fromTable.getTableRef().accept(this);

        return false;
    }

    @Override
    public boolean visit(LeftJoin leftJoin) {
        sql.append(" LEFT JOIN ");
        leftJoin.getJoinedTableRef().accept(this);
        sql.append(" ON ");
        leftJoin.getExpression().accept(this);

        return false;
    }

    @Override
    public void visit(BooleanLiteral booleanLiteral) {
        sql.append(booleanLiteral.getOriginal());
    }

    @Override
    public boolean visit(JoinInfo joinInfo) {
        joinInfo.getTableRef().accept(this);
        sql.append(" ON ");

        return false;
    }

    @Override
    public void visit(CountAll countAll) {
        sql.append("COUNT(*)");
    }

    @Override
    public boolean visit(FromJoinedTables fromJoinedTables) {
        fromJoinedTables.getTableRef().accept(this);
        for (JoinExpression joinExpression : fromJoinedTables.getJoinExpression()) {
            joinExpression.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(DisplayedColumn displayedColumn) {
        displayedColumn.getColumnSpec().accept(this);
        if (displayedColumn.getAlias() != null) {
            sql.append(" AS ");
            displayedColumn.getAlias().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(CountExpression countExpression) {
        sql.append("COUNT(");
        if (countExpression.getExpression() != null) {
            countExpression.getExpression().accept(this);
        }

        sql.append(")");

        return false;
    }

    @Override
    public boolean visit(InSelect inSelect) {
        inSelect.getOperand().accept(this);
        sql.append(" IN (");
        inSelect.getSelect().accept(this);
        sql.append(")");

        return false;
    }

    @Override
    public boolean visit(SUM sum) {
        sql.append("SUM(");
        if (sum.getExpression() != null) {
            sum.getExpression().accept(this);
        }
        sql.append(")");

        return false;
    }

    @Override
    public boolean visit(OperandCondition operandCondition) {
        operandCondition.getOperand().accept(this);

        return false;
    }

    @Override
    public void visit(Alias alias) {
        sql.append(escapeEntity).append(alias.getAlias()).append(escapeEntity);
    }

    @Override
    public void visit(DateLiteral dateLiteral) {
        sql.append(dateLiteral.getOriginal());
    }

    @Override
    public boolean visit(Default aDefault) {
        sql.append("DEFAULT ");

        return false;
    }

    @Override
    public void visit(FloatLiteral floatLiteral) {
        sql.append(floatLiteral.getOriginal());

    }

    @Override
    public void visit(DoubleLiteral doubleLiteral) {
        sql.append(doubleLiteral.getOriginal());
    }

    @Override
    public boolean visit(OrderBy orderBy) {
        sql.append(" ORDER BY ");
        for (Iterator<OrderByItem> orderByItemIterator = orderBy.getOrderByItems().iterator(); orderByItemIterator.hasNext();) {
            OrderByItem orderByItem = orderByItemIterator.next();

            orderByItem.accept(this);
            if (orderByItemIterator.hasNext()) {
                sql.append(", ");
            }
        }

        return false;
    }

    @Override
    public boolean visit(OrderByItem orderByItem) {
        for (Iterator<ColumnSpec> iterator = orderByItem.getColumns().iterator(); iterator.hasNext(); ) {
            iterator.next().accept(this);

            if (iterator.hasNext()) {
                sql.append(", ");
            }
        }
        if (orderByItem.isAsc()) {
            sql.append(" ASC ");
        } else {
            sql.append(" DESC ");
        }

        return false;
    }

    @Override
    public void visit(Limit limit) {
        sql.append(" LIMIT ").append(limit.getLimit());
    }

    @Override
    public void visit(Offset offset) {
        sql.append(" OFFSET ").append(offset.getOffset());
    }

    @Override
    public boolean visit(DisplayedOperand displayedOperand) {
        displayedOperand.getOperand().accept(this);
        if (displayedOperand.getAlias() != null) {
            sql.append(" AS ");
            displayedOperand.getAlias().accept(this);
        }

        return false;
    }

    @Override
    public void visit(InsertValues insertValues) {
        for (Iterator<UpdateValue> iterator = insertValues.getUpdateValues().iterator(); iterator.hasNext(); ) {
            UpdateValue updateValue = iterator.next();
            RValue value = updateValue.getValue();

            value.accept(this);
            if (iterator.hasNext()) {
                sql.append(",");
            }
        }
    }

    @Override
    public boolean visit(ForeignKeyConstraint foreignKeyConstraint) {
        sql
                .append("FOREIGN KEY (")
                .append(foreignKeyConstraint.getColumnName())
                .append(")")
                .append(" REFERENCES ")
                .append(escapeEntity)
                .append(foreignKeyConstraint.getTypeName())
                .append(escapeEntity)
                .append("(")
                .append(escapeEntity)
                .append(foreignKeyConstraint.getForeignColumnName())
                .append(escapeEntity)
                .append(")");

        return false;
    }

    @Override
    public boolean visit(CountColumn countColumn) {
        sql.append("COUNT(");
        countColumn.getColumnSpec().accept(this);
        sql.append(")");

        return false;
    }

    public void reset() {
        sql = new StringBuilder();
    }
}
