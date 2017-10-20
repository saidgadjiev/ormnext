package ru.said.miami.orm.core.query.visitor;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.*;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void start(SelectQuery tSelectQuery) {
        sql.append("SELECT * FROM ");
        tSelectQuery.getFrom().accept(this);
        if (!tSelectQuery.getWhere().getConditions().isEmpty()) {
            sql.append(" WHERE ");
        }
    }

    @Override
    public void finish(SelectQuery tSelectQuery) {

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
    public void start(AttributeDefenition attributeDefenition) {

    }

    @Override
    public void start(CreateTableQuery tCreateTableQuery) {
        sql.append("CREATE TABLE '").append(tCreateTableQuery.getTypeName()).append("' (");
        for (Iterator<AttributeDefenition> iterator = tCreateTableQuery.getAttributeDefenitions().iterator(); iterator.hasNext(); ) {
            AttributeDefenition attributeDefenition = iterator.next();

            sql.append("'").append(attributeDefenition.getName()).append("' ");
            appendAttributeDataType(attributeDefenition);
            if (attributeDefenition.isId()) {
                sql.append(" PRIMARY KEY");
                if (attributeDefenition.isGenerated()) {
                    sql.append(" AUTOINCREMENT");
                }
            }
            if (iterator.hasNext()) {
                sql.append(",");
            }
        }

        sql.append(");\n");
    }

    private void appendAttributeDataType(AttributeDefenition def) {
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

    }

    @Override
    public void finish(AttributeDefenition attributeDefenition) {

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
}
