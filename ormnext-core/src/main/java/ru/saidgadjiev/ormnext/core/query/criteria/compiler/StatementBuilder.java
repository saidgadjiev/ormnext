package ru.saidgadjiev.ormnext.core.query.criteria.compiler;

import ru.saidgadjiev.ormnext.core.dao.SessionCriteriaContract;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

/**
 * Statement builder.
 *
 * @author Said Gadjiev
 */
public class StatementBuilder {

    /**
     * Target session.
     */
    private final SessionCriteriaContract session;

    /**
     * Create a new instance.
     *
     * @param session target session
     */
    public StatementBuilder(SessionCriteriaContract session) {
        this.session = session;
    }

    /**
     * Create select statement.
     *
     * @param entityType entity class
     * @param <T> entity type
     * @return select statement
     */
    public <T> SelectStatement<T> createSelectStatement(Class<T> entityType) {
        return new SelectStatement<>(entityType, session);
    }

    /**
     * Create update statement.
     *
     * @param entityType entity class
     * @return update statement
     */
    public UpdateStatement createUpdateStatement(Class<?> entityType) {
        return new UpdateStatement(entityType, session);
    }

    /**
     * Create delete statement.
     *
     * @param entityType entity class
     * @return delete statement
     */
    public DeleteStatement createDeleteStatement(Class<?> entityType) {
        return new DeleteStatement(entityType, session);
    }

    /**
     * Create query.
     *
     * @param query target query
     * @return query
     */
    public Query createQuery(String query) {
        return new Query(query, session);
    }
}
