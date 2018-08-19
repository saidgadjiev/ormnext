package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionCriteriaContract;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Delete statement.
 *
 * @author Said Gadjiev
 */
public class DeleteStatement implements QueryElement, CriteriaStatement {

    /**
     * Entity class.
     */
    private final Class<?> entityClass;

    /**
     * Session.
     */
    private SessionCriteriaContract session;

    /**
     * Provided user args.
     */
    private final Map<Integer, Object> userProvidedArgs = new HashMap<>();

    /**
     * Where expression holder.
     *
     * @see Criteria
     */
    private Criteria where;

    /**
     * Create a new instance.
     *
     * @param entityClass target entity class
     * @param session target session
     */
    public DeleteStatement(Class<?> entityClass, SessionCriteriaContract session) {
        this.entityClass = entityClass;
        this.session = session;
    }

    /**
     * Provide where expression.
     *
     * @param where target where clause
     * @return this for chain
     */
    public DeleteStatement where(Criteria where) {
        this.where = where;

        return this;
    }

    @Override
    public List<CriterionArgument> getArgs() {
        List<CriterionArgument> args = new LinkedList<>();

        if (where != null) {
            args.addAll(where.getArgs());
        }

        return args;
    }

    @Override
    public void attach(Session session) {

    }

    @Override
    public Map<Integer, Object> getUserProvidedArgs() {
        return userProvidedArgs;
    }

    /**
     * Return entity class.
     *
     * @return entity class
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * Return where expression.
     *
     * @return where expression
     */
    public Expression getWhere() {
        return where == null ? null : where.expression();
    }

    /**
     * Execute current statement.
     *
     * @return deleted count
     * @throws SQLException any SQL exceptions
     */
    public int delete() throws SQLException {
        return session.delete(this);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (where != null) {
            where.accept(visitor);
        }
    }
}
