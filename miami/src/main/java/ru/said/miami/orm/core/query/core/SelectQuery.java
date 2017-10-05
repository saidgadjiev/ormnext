package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.field.FieldType;
import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import javax.swing.text.html.Option;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Класс SELECT запроса
 */
public class SelectQuery implements Query, QueryElement {

    private final QueryVisitor visitor;

    private TableRef from;

    private Expression where = new Expression();

    private SelectQuery(QueryVisitor visitor) {
        this.visitor = visitor;
    }

    public TableRef getFrom() {
        return from;
    }

    public void setFrom(TableRef from) {
        this.from = from;
    }

    public Expression getWhere() {
        return where;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IMiamiCollection execute(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        this.accept(visitor);
        ResultSet resultSet = statement.executeQuery(visitor.getQuery());

        return new IMiamiCollection() {
            @Override
            public boolean next() throws SQLException {
                return resultSet.next();
            }

            @Override
            public IMiamiData get() throws SQLException {
                return new IMiamiData() {
                    @Override
                    public boolean getBoolean(String name) throws SQLException {
                        return resultSet.getBoolean(name);
                    }

                    @Override
                    public int getInt(String name) throws SQLException {
                        return resultSet.getInt(name);
                    }

                    @Override
                    public String getString(String name) throws SQLException {
                        return resultSet.getString(name);
                    }

                    @Override
                    public Date getTime(String name) throws SQLException {
                        return resultSet.getTime(name);
                    }

                    @Override
                    public double getDouble(String name) throws SQLException {
                        return resultSet.getDouble(name);
                    }

                    @Override
                    public Object getObject(String name) throws SQLException {
                        return resultSet.getObject(name);
                    }
                };
            }

            @Override
            public void close() throws SQLException {
                resultSet.close();
            }
        };
    }

    public static<T> SelectQuery buildQueryById(String typeName, FieldType idField, T id) {
        SelectQuery selectQuery = new SelectQuery(new DefaultVisitor());
        selectQuery.setFrom(new TableRef(typeName));
        AndCondition andCondition = new AndCondition();

        andCondition.add(new Equals(new ColumnSpec(idField.getFieldName()), idField.getDataPersister().getAssociatedOperand(id)));
        selectQuery.getWhere().getConditions().add(andCondition);

        return selectQuery;
    }

    public static<T> SelectQuery buildQueryForAll(String typeName) {
        SelectQuery selectQuery = new SelectQuery(new DefaultVisitor());
        selectQuery.setFrom(new TableRef(typeName));

        return selectQuery;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        where.accept(visitor);
        visitor.finish(this);
    }
}
