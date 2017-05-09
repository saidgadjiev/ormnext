package clause;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by said on 07.05.17.
 */
public class UpdateTest {
    @Test
    public void addUpdateColumn() throws Exception {
        Update update = new Update();

        update.addUpdateColumn("name", "test_foo");
        Assert.assertEquals(" SET name='test_foo'", update.getStringQuery());
    }

    @Test
    public void setWhere() throws Exception {
        Update update = new Update();

        update.addUpdateColumn("name", "test_foo");
        Where where = new Where();

        where.addEqClause("id", 1);
        update.setWhere(where);
        Assert.assertEquals(" SET name='test_foo' WHERE id='1'", update.getStringQuery());
    }

}