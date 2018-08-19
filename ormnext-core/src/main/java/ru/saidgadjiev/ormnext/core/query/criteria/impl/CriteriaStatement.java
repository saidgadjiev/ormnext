package ru.saidgadjiev.ormnext.core.query.criteria.impl;

import ru.saidgadjiev.ormnext.core.dao.Session;

import java.util.List;
import java.util.Map;

/**
 * Interface for criteria statements.
 *
 * @author Said Gadjiev
 */
public interface CriteriaStatement {

    /**
     * Return user provided args.
     *
     * @return user provided args
     */
    Map<Integer, Object> getUserProvidedArgs();

    /**
     * Return criterion args.
     *
     * @return criterion args
     */
    List<CriterionArgument> getArgs();

    /**
     * Attach session to current statement.
     *
     * @param session target session
     */
    void attach(Session session);
}
