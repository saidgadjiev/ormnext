package ru.said.orm.next.core.query.visitor;

import ru.said.orm.next.core.db.DatabaseType;
import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.query.core.*;
import ru.said.orm.next.core.query.core.clause.GroupBy;
import ru.said.orm.next.core.query.core.clause.GroupByItem;
import ru.said.orm.next.core.query.core.clause.Having;
import ru.said.orm.next.core.query.core.clause.from.FromJoinedTables;
import ru.said.orm.next.core.query.core.clause.from.FromTable;
import ru.said.orm.next.core.query.core.clause.select.SelectAll;
import ru.said.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.said.orm.next.core.query.core.column_spec.ColumnSpec;
import ru.said.orm.next.core.query.core.column_spec.DisplayedColumnSpec;
import ru.said.orm.next.core.query.core.column_spec.DisplayedColumns;
import ru.said.orm.next.core.query.core.common.TableRef;
import ru.said.orm.next.core.query.core.common.UpdateValue;
import ru.said.orm.next.core.query.core.condition.*;
import ru.said.orm.next.core.query.core.constraints.attribute.AttributeConstraint;
import ru.said.orm.next.core.query.core.constraints.attribute.NotNullConstraint;
import ru.said.orm.next.core.query.core.constraints.attribute.PrimaryKeyConstraint;
import ru.said.orm.next.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.said.orm.next.core.query.core.constraints.table.UniqueConstraint;
import ru.said.orm.next.core.query.core.function.*;
import ru.said.orm.next.core.query.core.join.JoinExpression;
import ru.said.orm.next.core.query.core.join.JoinInfo;
import ru.said.orm.next.core.query.core.join.LeftJoin;
import ru.said.orm.next.core.query.core.literals.*;

import java.util.Iterator;

/**
 * Visitor по умолчанию
 */
public class DefaultVisitor implements QueryVisitor {

    private StringBuilder sql = new StringBuilder();

    private DatabaseType databaseType;

    public DefaultVisitor(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public String getQuery() {
        return sql.toString();
    }

    @Override
    public boolean start(CreateQuery tCreateQuery) {
        sql.append("INSERT INTO `").append(tCreateQuery.getTypeName()).append("`");

        if (tCreateQuery.getUpdateValues().isEmpty()) {
            sql.append(" ").append(databaseType.appendNoColumn());

            return false;
        } else {
            sql.append(" (");
        }

        for (Iterator<UpdateValue> iterator = tCreateQuery.getUpdateValues().iterator(); iterator.hasNext(); ) {
            UpdateValue updateValue = iterator.next();

            sql.append("`").append(updateValue.getName()).append("`");
            if (iterator.hasNext()) {
                sql.append(",");
            }
        }
        sql.append(")");
        sql.append(" VALUES (");
        for (Iterator<UpdateValue> iterator = tCreateQuery.getUpdateValues().iterator(); iterator.hasNext(); ) {
            UpdateValue updateValue = iterator.next();
            RValue value = updateValue.getValue();

            value.accept(this);
            if (iterator.hasNext()) {
                sql.append(",");
            }
        }
        sql.append(")");

        return false;
    }

    @Override
    public void finish(CreateQuery tCreateQuery) {

    }

    @Override
    public void start(UpdateValue updateValue) {

    }

    @Override
    public void finish(UpdateValue updateValue) {

    }

    @Override
    public void start(StringLiteral stringLiteral) {
        sql.append("'").append(stringLiteral.getValue()).append("'");
    }

    @Override
    public void finish(StringLiteral stringLiteral) {

    }

    @Override
    public void start(Select tSelectQuery) {
        sql.append("SELECT ");
        if (tSelectQuery.getSelectColumnsStrategy() != null) {
            tSelectQuery.getSelectColumnsStrategy().accept(this);
        }
        sql.append(" FROM ");
        if (tSelectQuery.getFrom() != null) {
            tSelectQuery.getFrom().accept(this);
        }
        if (!tSelectQuery.getWhere().getConditions().isEmpty()) {
            sql.append(" WHERE ");
        }
    }

    @Override
    public void finish(Select tSelectQuery) {

    }

    @Override
    public void start(Expression expression) {
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
    }

    @Override
    public void finish(Expression expression) {

    }

    @Override
    public void start(AndCondition andCondition) {

    }

    @Override
    public void finish(AndCondition andCondition) {

    }

    @Override
    public void start(Equals equals) {
        equals.getFirst().accept(this);
        sql.append(" = ");
        equals.getSecond().accept(this);
    }

    @Override
    public void finish(Equals equals) {

    }

    @Override
    public void start(ColumnSpec columnSpec) {
        if (columnSpec.getAlias() != null) {
            columnSpec.getAlias().accept(this);
            sql.append(".");
        }
        sql.append("`").append(columnSpec.getName()).append("`");
    }

    @Override
    public void finish(ColumnSpec columnSpec) {

    }

    @Override
    public void finish(TableRef tableRef) {

    }

    @Override
    public void start(TableRef tableRef) {
        sql.append("`").append(tableRef.getTableName()).append("`");

        if (tableRef.getAlias() != null) {
            sql.append(" AS ");
            tableRef.getAlias().accept(this);
        }
    }

    @Override
    public void start(AttributeDefinition attributeDefinition) {

    }

    @Override
    public void start(CreateTableQuery tCreateTableQuery) {
        sql.append("CREATE TABLE ");
        if (tCreateTableQuery.isIfNotExists()) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append("`").append(tCreateTableQuery.getTypeName()).append("` (");
        for (Iterator<AttributeDefinition> iterator = tCreateTableQuery.getAttributeDefinitions().iterator(); iterator.hasNext(); ) {
            AttributeDefinition attributeDefinition = iterator.next();

            sql.append("`").append(attributeDefinition.getName()).append("` ");
            appendAttributeDataType(attributeDefinition);
            for (AttributeConstraint attributeConstraint: attributeDefinition.getAttributeConstraints()) {
                attributeConstraint.accept(this);
            }
            if (iterator.hasNext()) {
                sql.append(", ");
            }
        }
    }

    private void appendAttributeDataType(AttributeDefinition def) {
        DataType dataType = def.getDataType();

        switch (dataType) {
            case STRING:
                sql.append("VARCHAR").append("(").append(def.getLength()).append(")");
                break;
            case INTEGER:
                sql.append("INTEGER");
                break;
            case BOOLEAN:
                sql.append("BOOLEAN");
                break;
            case DATE:
                sql.append("TIMESTAMP");
                break;
            case UNKNOWN:
                break;
        }


    }

    @Override
    public void finish(CreateTableQuery tCreateTableQuery) {
        sql.append(")");
    }

    @Override
    public void finish(AttributeDefinition attributeDefinition) {

    }

    @Override
    public void start(DeleteQuery deleteQuery) {
        sql.append("DELETE FROM ").append(deleteQuery.getTypeName());
        if (!deleteQuery.getWhere().getConditions().isEmpty()) {
            sql.append(" WHERE ");
        }
    }

    @Override
    public void finish(DeleteQuery deleteQuery) {

    }

    @Override
    public void start(IntLiteral intLiteral) {
        sql.append(intLiteral.getValue());
    }

    @Override
    public void finish(IntLiteral intLiteral) {

    }

    @Override
    public boolean start(UpdateQuery updateQuery) {
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
        }

        return false;
    }

    @Override
    public void finish(UpdateQuery updateQuery) {

    }

    @Override
    public void start(DropTableQuery dropTableQuery) {
        sql.append("DROP TABLE ");
        if (dropTableQuery.isIfExists()) {
            sql.append("IF EXISTS ");
        }
        sql.append("`").append(dropTableQuery.getTableName()).append("`");
    }

    @Override
    public void finish(DropTableQuery dropTableQuery) {

    }

    @Override
    public void start(PrimaryKeyConstraint primaryKeyConstraint) {
        sql.append(databaseType.appendPrimaryKey(primaryKeyConstraint.isGenerated()));
    }

    @Override
    public void finish(PrimaryKeyConstraint primaryKeyConstraint) {

    }

    @Override
    public void start(UniqueConstraint uniqueConstraint) {
        sql.append(", UNIQUE (`");
        for (Iterator<String> iterator = uniqueConstraint.getUniqueColemns().iterator(); iterator.hasNext();) {
            sql.append(iterator.next());
            if (iterator.hasNext()) {
                sql.append("`,`");
            }
        }
        sql.append("`)");
    }

    @Override
    public void finish(UniqueConstraint uniqueConstraint) {

    }

    @Override
    public void start(NotNullConstraint notNullConstraint) {
        sql.append("NOT NULL");
    }

    @Override
    public void finish(NotNullConstraint notNullConstraint) {

    }

    @Override
    public void start(ReferencesConstraint referencesConstraint) {
        sql.append(" REFERENCES `")
                .append(referencesConstraint.getTypeName())
                .append("`(`")
                .append(referencesConstraint.getColumnName())
                .append("`)");
    }

    @Override
    public void finish(ReferencesConstraint referencesConstraint) {

    }

    @Override
    public void start(CreateIndexQuery createIndexQuery) {
        sql.append("CREATE ")
                .append(createIndexQuery.isUnique() ? "UNIQUE": "INDEX")
                .append(" `")
                .append(createIndexQuery.getIndexName())
                .append("` ON `")
                .append(createIndexQuery.getTableName())
                .append("`(`");
        for (Iterator<String> iterator = createIndexQuery.getColumns().iterator(); iterator.hasNext();) {
            sql.append(iterator.next());

            if (iterator.hasNext()) {
                sql.append("`,`");
            }
        }
        sql.append("`)");
    }

    @Override
    public void finish(CreateIndexQuery createIndexQuery) {

    }

    @Override
    public void start(DropIndexQuery dropIndexQuery) {
        sql.append("DROP INDEX `").append(dropIndexQuery.getName()).append("`");
    }

    @Override
    public void finish(DropIndexQuery dropIndexQuery) {

    }

    @Override
    public void start(Param param) {
        sql.append("?");
    }

    @Override
    public void finish(Param param) {

    }

    @Override
    public void start(SelectAll selectAll) {
        sql.append("*");
    }

    @Override
    public void finish(SelectAll selectAll) {

    }

    @Override
    public void start(SelectColumnsList selectColumnsList) {
        for (Iterator<DisplayedColumnSpec> columnIterator = selectColumnsList.getColumns().iterator(); columnIterator.hasNext();) {
            DisplayedColumnSpec columnSpec = columnIterator.next();

            columnSpec.accept(this);
            if (columnIterator.hasNext()) {
                sql.append(", ");
            }
        }
    }

    @Override
    public void finish(SelectColumnsList selectColumnsList) {

    }

    @Override
    public void start(Having having) {
        sql.append(" HAVING ");

    }

    @Override
    public void finish(GroupBy groupBy) {

    }

    @Override
    public void start(GroupBy groupBy) {
        sql.append(" GROUP BY ");

        for (Iterator<GroupByItem> columnSpecIterator = groupBy.getGroupByItems().iterator(); columnSpecIterator.hasNext();) {
            GroupByItem columnSpec = columnSpecIterator.next();

            columnSpec.accept(this);
            if (columnSpecIterator.hasNext()) {
                sql.append(",");
            }
        }
    }

    @Override
    public void finish(Having having) {

    }

    @Override
    public void finish(FromTable fromTable) {
    }

    @Override
    public void start(FromTable fromTable) {
    }

    @Override
    public void start(LeftJoin leftJoin) {
        sql.append(" LEFT JOIN ");
        leftJoin.getJoinedTableRef().accept(this);
        sql.append(" ON ");
    }

    @Override
    public void finish(LeftJoin leftJoin) {
    }

    @Override
    public void finish(BooleanLiteral booleanLiteral) {

    }

    @Override
    public void start(BooleanLiteral booleanLiteral) {
        sql.append(String.valueOf(booleanLiteral.getValue()));
    }

    @Override
    public void start(JoinInfo joinInfo) {
        joinInfo.getTableRef().accept(this);
        sql.append(" ON ");
    }

    @Override
    public void finish(JoinInfo joinInfo) {

    }

    @Override
    public void start(CountAll countAll) {

    }

    @Override
    public void finish(CountAll countAll) {
        sql.append("COUNT(*)");
    }

    @Override
    public void start(FromJoinedTables fromJoinedTables) {
        fromJoinedTables.getTableRef().accept(this);
        for (JoinExpression joinExpression: fromJoinedTables.getJoinExpression()) {
            joinExpression.accept(this);
        }
    }

    @Override
    public void finish(FromJoinedTables fromJoinedTables) {

    }

    @Override
    public void start(DisplayedColumns displayedColumns) {
        if (displayedColumns.getAlias() != null) {
            displayedColumns.getAlias().accept(this);
            sql.append(".");
        }
        sql.append(displayedColumns.getColumnSpec().getName());
    }

    @Override
    public void finish(DisplayedColumns displayedColumns) {

    }

    @Override
    public void start(AVG avg) {

    }

    @Override
    public void finish(AVG avg) {

    }

    @Override
    public void start(CountExpression countExpression) {
        sql.append("COUNT(");
    }

    @Override
    public void finish(CountExpression countExpression) {
        sql.append(")");
    }

    @Override
    public void start(MAX max) {

    }

    @Override
    public void finish(MAX max) {

    }

    @Override
    public void start(MIN min) {

    }

    @Override
    public void finish(MIN min) {

    }

    @Override
    public void start(Exists exists) {

    }

    @Override
    public void finish(Exists exists) {

    }

    @Override
    public void start(InSelect inSelect) {

    }

    @Override
    public void finish(InSelect inSelect) {

    }

    @Override
    public void start(NotInSelect notInSelect) {

    }

    @Override
    public void finish(NotInSelect notInSelect) {

    }

    @Override
    public void start(GreaterThan greaterThan) {

    }

    @Override
    public void finish(GreaterThan greaterThan) {

    }

    @Override
    public void start(GreaterThanOrEquals greaterThanOrEquals) {

    }

    @Override
    public void finish(GreaterThanOrEquals greaterThanOrEquals) {

    }

    @Override
    public void start(LessThan lessThan) {

    }

    @Override
    public void finish(LessThan lessThan) {

    }

    @Override
    public void start(LessThanOrEquals lessThanOrEquals) {

    }

    @Override
    public void finish(LessThanOrEquals lessThanOrEquals) {

    }

    @Override
    public void start(SUM sum) {
        sql.append("SUM(");
    }

    @Override
    public void finish(SUM sum) {
        sql.append(")");
    }

    @Override
    public void start(OperandCondition operandCondition) {
        operandCondition.getOperand().accept(this);
    }

    @Override
    public void finish(OperandCondition operandCondition) {

    }

    @Override
    public void start(Alias alias) {
        sql.append("`").append(alias.getAlias()).append("`");
    }

    @Override
    public void finish(Alias alias) {

    }

    @Override
    public void start(TimeLiteral timeLiteral) {

    }

    @Override
    public void finish(TimeLiteral timeLiteral) {

    }
}
