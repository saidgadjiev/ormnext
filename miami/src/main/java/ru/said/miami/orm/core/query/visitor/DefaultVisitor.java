package ru.said.miami.orm.core.query.visitor;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.*;
import ru.said.miami.orm.core.query.core.conditions.Condition;
import ru.said.miami.orm.core.query.core.conditions.Equals;
import ru.said.miami.orm.core.query.core.conditions.Expression;
import ru.said.miami.orm.core.query.core.constraints.attribute.AttributeConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.GeneratedConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.NotNullConstraint;
import ru.said.miami.orm.core.query.core.constraints.attribute.ReferencesConstraint;
import ru.said.miami.orm.core.query.core.constraints.table.UniqueConstraint;
import ru.said.miami.orm.core.query.core.defenitions.AttributeDefinition;
import ru.said.miami.orm.core.query.core.literals.IntLiteral;
import ru.said.miami.orm.core.query.core.literals.Param;
import ru.said.miami.orm.core.query.core.literals.RValue;
import ru.said.miami.orm.core.query.core.literals.StringLiteral;
import ru.said.miami.orm.core.query.core.sqlQuery.*;

import java.util.Iterator;

/**
 * Visitor по умолчанию
 */
public class DefaultVisitor implements QueryVisitor {

    private StringBuilder sql = new StringBuilder();

    @Override
    public String getQuery() {
        return sql.toString();
    }

    @Override
    public boolean start(CreateQuery tCreateQuery) {
        sql.append("INSERT INTO '").append(tCreateQuery.getTypeName()).append("' (");
        for (Iterator<UpdateValue> iterator = tCreateQuery.getUpdateValues().iterator(); iterator.hasNext(); ) {
            UpdateValue updateValue = iterator.next();

            sql.append("'").append(updateValue.getName()).append("'");
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
        tSelectQuery.getSelectExpression().accept(this);
        sql.append(" FROM ");
        tSelectQuery.getFrom().accept(this);
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
        sql.append("=");
        equals.getSecond().accept(this);
    }

    @Override
    public void finish(Equals equals) {

    }

    @Override
    public void start(ColumnSpec columnSpec) {
        sql.append(columnSpec.getName());
    }

    @Override
    public void finish(ColumnSpec columnSpec) {

    }

    @Override
    public void finish(TableRef tableRef) {

    }

    @Override
    public void start(TableRef tableRef) {
        sql.append("'").append(tableRef.getTypeName()).append("'");
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
        sql.append("'").append(tCreateTableQuery.getTypeName()).append("' (");
        for (Iterator<AttributeDefinition> iterator = tCreateTableQuery.getAttributeDefinitions().iterator(); iterator.hasNext(); ) {
            AttributeDefinition attributeDefinition = iterator.next();

            sql.append("'").append(attributeDefinition.getName()).append("' ");
            appendAttributeDataType(attributeDefinition);
            for (AttributeConstraint attributeConstraint: attributeDefinition.getAttributeConstraints()) {
                attributeConstraint.accept(this);
            }
            if (iterator.hasNext()) {
                sql.append(",");
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
        sql.append(");\n");
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
        sql.append("'").append(dropTableQuery.getTableName()).append("'");
    }

    @Override
    public void finish(DropTableQuery dropTableQuery) {

    }

    @Override
    public void start(GeneratedConstraint generatedConstraint) {
        sql.append(" PRIMARY KEY AUTOINCREMENT ");
    }

    @Override
    public void finish(GeneratedConstraint generatedConstraint) {

    }

    @Override
    public void start(UniqueConstraint uniqueConstraint) {
        sql.append(", UNIQUE ('");
        for (Iterator<String> iterator = uniqueConstraint.getUniqueColemns().iterator(); iterator.hasNext();) {
            sql.append(iterator.next());
            if (iterator.hasNext()) {
                sql.append("','");
            }
        }
        sql.append("')");
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
        sql.append(" REFERENCES '")
                .append(referencesConstraint.getTypeName())
                .append("'('")
                .append(referencesConstraint.getColumnName())
                .append("')");
    }

    @Override
    public void finish(ReferencesConstraint referencesConstraint) {

    }

    @Override
    public void start(CreateIndexQuery createIndexQuery) {
        sql.append("CREATE ")
                .append(createIndexQuery.isUnique() ? "UNIQUE": "INDEX")
                .append(" '")
                .append(createIndexQuery.getIndexName())
                .append("' ON '")
                .append(createIndexQuery.getTableName())
                .append("'('");
        for (Iterator<String> iterator = createIndexQuery.getColumns().iterator(); iterator.hasNext();) {
            sql.append(iterator.next());

            if (iterator.hasNext()) {
                sql.append("','");
            }
        }
        sql.append("')");
    }

    @Override
    public void finish(CreateIndexQuery createIndexQuery) {

    }

    @Override
    public void start(DropIndexQuery dropIndexQuery) {
        sql.append("DROP INDEX '").append(dropIndexQuery.getName()).append("'");
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
    public void start(SelectColumns selectColumns) {
        for (Iterator<String> columnIterator = selectColumns.getColumns().iterator(); columnIterator.hasNext();) {
            sql.append(columnIterator.next());

            if (columnIterator.hasNext()) {
                sql.append(",");
            }
        }
    }

    @Override
    public void finish(SelectColumns selectColumns) {

    }
}
