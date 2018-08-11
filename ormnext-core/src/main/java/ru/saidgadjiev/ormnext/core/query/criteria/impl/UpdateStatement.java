package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionCriteriaContract;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.Expression;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.Param;

import java.sql.SQLException;
import java.util.*;

/**
 * Delete statement.
 *
 * @author Said Gadjiev
 */
public class UpdateStatement implements QueryElement, CriteriaStatement {

    /**
     * Entity class.
     */
    private final Class<?> entityClass;
    private SessionCriteriaContract session;

    /**
     * Update value args.
     */
    private List<CriterionArgument> updateArgs = new ArrayList<>();

    /**
     * Update values.
     */
    private List<UpdateValue> updateValues = new ArrayList<>();

    /**
     * User provided args.
     */
    private Map<Integer, Object> userProvidedArgs = new HashMap<>();

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
     */
    public UpdateStatement(Class<?> entityClass, SessionCriteriaContract session) {
        this.entityClass = entityClass;
        this.session = session;
    }

    /**
     * Provide update value.
     *
     * @param propertyName property name
     * @param value value
     * @return this instance for chain
     */
    public UpdateStatement set(String propertyName, Object value) {
        updateArgs.add(new CriterionArgument(propertyName, value));
        updateValues.add(new UpdateValue(propertyName, new Param()));

        return this;
    }

    /**
     * Provide where expression.
     *
     * @param where target where clause
     * @return this for chain
     */
    public UpdateStatement where(Criteria where) {
        this.where = where;

        return this;
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
     * Return update values.
     *
     * @return update values
     */
    public List<UpdateValue> getUpdateValues() {
        return updateValues;
    }

    /**
     * Provide user arg.
     *
     * @param index arg index
     * @param arg arg value
     */
    public void setArg(Integer index, Object arg) {
        userProvidedArgs.put(index, arg);
    }

    @Override
    public List<CriterionArgument> getArgs() {
        List<CriterionArgument> args = new LinkedList<>();

        args.addAll(updateArgs);
        if (where != null) {
            args.addAll(where.getArgs());
        }

        return args;
    }

    @Override
    public void attach(Session session) {
        this.session = (SessionCriteriaContract) session;
    }

    @Override
    public Map<Integer, Object> getUserProvidedArgs() {
        return userProvidedArgs;
    }

    public int update() throws SQLException {
        return session.update(this);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (where != null) {
            where.accept(visitor);
        }
    }
}
