package ru.saidgadjiev.ormnext.core.criteria.impl;

import ru.saidgadjiev.ormnext.core.query.core.UpdateQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

/**
 * Created by said on 23.02.2018.
 */
@SuppressWarnings("PMD")
public class UpdateStatement<T> implements QueryElement {

    private UpdateQuery updateQuery;

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
