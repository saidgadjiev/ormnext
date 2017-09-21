package ru.said.miami.orm.core.query.visitor;

import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.query.core.*;

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
    public void start(CreateQuery tCreateQuery) {
        sql.append("INSERT INTO ").append(tCreateQuery.getTypeName()).append(" (");
        for (Iterator<UpdateValue> iterator = tCreateQuery.getUpdateValues().iterator(); iterator.hasNext(); ) {
            UpdateValue updateValue = iterator.next();

            sql.append(updateValue.getName());
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
        sql.append("\'").append(stringLiteral.getValue()).append("\'");
    }

    @Override
    public void finish(StringLiteral stringLiteral) {

    }

    @Override
    public void start(SelectQuery tSelectQuery) {

    }

    @Override
    public void finish(SelectQuery tSelectQuery) {

    }

    @Override
    public void start(Expression expression) {

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

    }

    @Override
    public void finish(Equals equals) {

    }

    @Override
    public void start(ColumnSpec columnSpec) {

    }

    @Override
    public void finish(ColumnSpec columnSpec) {

    }

    @Override
    public void finish(TableRef tableRef) {

    }

    @Override
    public void start(TableRef tableRef) {

    }

    @Override
    public void start(AttributeDefenition attributeDefenition) {

    }

    @Override
    public void start(CreateTableQuery tCreateTableQuery) {
        sql.append("CREATE TABLE ").append(tCreateTableQuery.getTypeName()).append(" (");
        for (Iterator<AttributeDefenition> iterator = tCreateTableQuery.getAttributeDefenitions().iterator(); iterator.hasNext();) {
            AttributeDefenition attributeDefenition = iterator.next();

            sql.append(attributeDefenition.getName()).append(" ");
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
                sql.append("INT");
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
}
