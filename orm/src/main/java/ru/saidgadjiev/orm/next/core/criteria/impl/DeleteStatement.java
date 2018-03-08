package ru.saidgadjiev.orm.next.core.criteria.impl;

import ru.saidgadjiev.orm.next.core.query.core.DeleteQuery;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

/**
 * Created by said on 23.02.2018.
 */
@SuppressWarnings("PMD")
public class DeleteStatement<T> implements QueryElement {

    private DeleteQuery deleteQuery;

    @Override
    public void accept(QueryVisitor visitor) {

    }
}
