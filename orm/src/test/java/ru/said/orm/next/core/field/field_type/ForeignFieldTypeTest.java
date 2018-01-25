package ru.said.orm.next.core.field.field_type;

import org.junit.Assert;
import org.junit.Test;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.DataType;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class ForeignFieldTypeTest {

    @Test
    public void isForeignAutoCreate() throws Exception {
        Field field = DBFieldTypeTest.TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertTrue(fieldType.isForeignAutoCreate());
        field = DBFieldTypeTest.TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertFalse(fieldType.isForeignAutoCreate());
    }

    @Test
    public void getForeignFieldClass() {
    }

    @Test
    public void getDataPersister() {
    }

    @Test
    public void getDataType() {
    }

    @Test
    public void getForeignPrimaryKey() {
    }

    @Test
    public void getColumnName() {
    }

    @Test
    public void getForeignTableName() {
    }

    @Test
    public void getForeignColumnName() {
    }

    public static class TestClazz {
        @DBField(foreignAutoCreate = true)
        private int field1;

        @DBField(foreignAutoCreate = false)
        private String field2;
    }
}