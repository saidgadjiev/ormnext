package ru.saidgadjiev.ormnext.core.query.criteria;

import ru.saidgadjiev.ormnext.core.dao.SessionCriteriaContract;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Query;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;

/**
 * Created by said on 11.08.2018.
 */
public class StatementBuilder {

    private final SessionCriteriaContract session;

    public StatementBuilder(SessionCriteriaContract session) {
        this.session = session;
    }

    public <T> SelectStatement<T> createSelectStatement(Class<T> entityType) {
        return new SelectStatement<>(entityType, session);
    }

    public UpdateStatement createUpdateStatement(Class<?> entityType) {
        return new UpdateStatement(entityType, session);
    }

    public DeleteStatement createDeleteStatement(Class<?> entityType) {
        return new DeleteStatement(entityType, session);
    }

    public Query createQuery(String query) {
        return new Query(query, session);
    }
}
