package ru.said.miami.orm.core.query.core;

import ru.said.miami.orm.core.query.visitor.DefaultVisitor;
import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс UPDATE запроса
 * @param <T> тип результата выполнения
 */

public class UpdateQuery implements Query, QueryElement {

    private QueryVisitor visitor;

    private String typeName;

    private UpdateQuery(String typeName, QueryVisitor visitor) {
        this.typeName = typeName;
        this.visitor = visitor;
    }

    @Override
    public <T> T execute(Connection connection) throws SQLException {
        this.accept(visitor);
        String sql = visitor.getQuery();

        return null;
    }

    /**
     * Создание экземпляра UpdateQuery
     * @return возвращет экземляр UpdateQuery
     */
    public static UpdateQuery buildQuery(String typeName) {
        return new UpdateQuery(typeName, new DefaultVisitor());
    }

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
