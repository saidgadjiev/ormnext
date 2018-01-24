package ru.said.orm.next.core.field.field_type;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import ru.said.orm.next.core.db.DatabaseType;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.persisters.IntegerDataPersister;
import ru.said.orm.next.core.field.persisters.StringDataPersister;
import ru.said.orm.next.core.table.DBTable;

import java.lang.reflect.Field;

public class ForeignFieldTypeTest {

    @Test
    public void isForeignAutoCreate() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertTrue(fieldType.isForeignAutoCreate());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertFalse(fieldType.isForeignAutoCreate());
    }

    @Test
    public void getForeignFieldClass() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertEquals(ForeignTestClazz1.class, fieldType.getForeignFieldClass());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertEquals(ForeignTestClazz2.class, fieldType.getForeignFieldClass());
    }

    @Test
    public void getDataPersister() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertThat(fieldType.getDataPersister(), CoreMatchers.instanceOf(IntegerDataPersister.class));
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertThat(fieldType.getDataPersister(), CoreMatchers.instanceOf(StringDataPersister.class));
    }

    @Test
    public void getDataType() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertThat(fieldType.getDataType(), CoreMatchers.is(DataType.INTEGER));
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertThat(fieldType.getDataType(), CoreMatchers.is(DataType.STRING));
    }

    @Test
    public void getForeignPrimaryKey() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        Field foreignPrimaryKeyField1 = ForeignTestClazz1.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertEquals(fieldType.getForeignPrimaryKey().getField(), foreignPrimaryKeyField1);
        field = TestClazz.class.getDeclaredFields()[1];
        Field foreignPrimaryKeyField2 = ForeignTestClazz2.class.getDeclaredFields()[0];
        fieldType = ForeignFieldType.build(field);

        Assert.assertEquals(fieldType.getForeignPrimaryKey().getField(), foreignPrimaryKeyField2);
    }

    @Test
    public void getColumnName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertEquals("field1_id", fieldType.getColumnName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertEquals("field2_id", fieldType.getColumnName());
    }

    @Test
    public void getForeignTableName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertEquals("ForeignTestClazz1", fieldType.getForeignTableName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertEquals("ForeignTestClazz2", fieldType.getForeignTableName());
    }

    @Test
    public void getForeignColumnName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignFieldType fieldType = ForeignFieldType.build(field);

        Assert.assertEquals("id", fieldType.getForeignColumnName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = ForeignFieldType.build(field);

        Assert.assertEquals("stringId", fieldType.getForeignColumnName());
    }

    public static class TestClazz {
        @DBField(foreign = true, foreignAutoCreate = true)
        private ForeignTestClazz1 field1;

        @DBField(foreign = true, foreignAutoCreate = false)
        private ForeignTestClazz2 field2;
    }

    @DBTable(name = "ForeignTestClazz1")
    public static class ForeignTestClazz1 {
        @DBField(id = true)
        private int id;
    }

    @DBTable(name = "ForeignTestClazz2")
    public static class ForeignTestClazz2 {
        @DBField(id = true, columnName = "stringId")
        private String stringId;
    }
}