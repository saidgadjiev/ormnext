package ru.said.miami.orm.core.cache.core.clause;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by said on 07.05.17.
 */
public class WhereTest {
    @Test
    public void addEqClause() throws Exception {
        Where where = new Where();

        where.addEqClause("name", "test_foo");
        Assert.assertEquals(" WHERE name='test_foo'", where.getStringQuery());
    }

}