package ru.saidgadjiev.ormnext.core.query.criteria.impl;

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
}
