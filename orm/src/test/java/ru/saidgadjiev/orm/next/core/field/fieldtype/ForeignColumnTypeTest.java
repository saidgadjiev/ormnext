package ru.saidgadjiev.orm.next.core.field.fieldtype;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.ForeignColumn;
import ru.saidgadjiev.orm.next.core.field.persister.IntegerDataPersister;
import ru.saidgadjiev.orm.next.core.field.persister.StringDataPersister;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntity;

import java.lang.reflect.Field;

public class ForeignColumnTypeTest {

    @Test
    public void isForeignAutoCreate() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertTrue(fieldType.isForeignAutoCreate());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertFalse(fieldType.isForeignAutoCreate());
    }

    @Test
    public void getForeignFieldClass() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(ForeignTestClazz1.class, fieldType.getForeignFieldClass());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(ForeignTestClazz2.class, fieldType.getForeignFieldClass());
    }

    @Test
    public void getDataPersister() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertThat(fieldType.getDataPersister(), CoreMatchers.instanceOf(IntegerDataPersister.class));
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertThat(fieldType.getDataPersister(), CoreMatchers.instanceOf(StringDataPersister.class));
    }

    @Test
    public void getDataType() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertThat(fieldType.getDataType(), CoreMatchers.is(DataType.INTEGER));
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertThat(fieldType.getDataType(), CoreMatchers.is(DataType.STRING));
    }

    @Test
    public void getForeignPrimaryKey() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        Field foreignPrimaryKeyField1 = ForeignTestClazz1.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(fieldType.getForeignPrimaryKey().getField(), foreignPrimaryKeyField1);
        field = TestClazz.class.getDeclaredFields()[1];
        Field foreignPrimaryKeyField2 = ForeignTestClazz2.class.getDeclaredFields()[0];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(fieldType.getForeignPrimaryKey().getField(), foreignPrimaryKeyField2);
    }

    @Test
    public void getColumnName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals("field1_id", fieldType.getColumnName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals("field2_id", fieldType.getColumnName());
    }

    @Test
    public void getForeignTableName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals("ForeignTestClazz1", fieldType.getForeignTableName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals("ForeignTestClazz2", fieldType.getForeignTableName());
    }

    @Test
    public void getForeignColumnName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignColumnType fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals("id", fieldType.getForeignColumnName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (ForeignColumnType) new ForeignColumnTypeFactory().createFieldType(field);

        Assert.assertEquals("stringId", fieldType.getForeignColumnName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongType() throws Exception {
        Field field = WrongTypeForeign.class.getDeclaredFields()[0];
        new ForeignColumnTypeFactory().createFieldType(field);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForeignPrimaryKeyMissed() throws Exception {
        Field field = WrongTypeForeign.class.getDeclaredFields()[1];
        new ForeignColumnTypeFactory().createFieldType(field);
    }

    private static class WrongType {
        @DatabaseColumn(id = true, dataType = DataType.INTEGER)
        private String field;
    }

    private static class PrimaryKeyMissed {
        @DatabaseColumn
        private String field;
    }

    private static class WrongTypeForeign {
        @ForeignColumn
        private WrongType wrongType;

        @ForeignColumn
        private PrimaryKeyMissed primaryKeyMissed;
    }

    private static class TestClazz {
        @ForeignColumn(foreignAutoCreate = true)
        private ForeignTestClazz1 field1;

        @ForeignColumn
        private ForeignTestClazz2 field2;
    }

    @DatabaseEntity(name = "ForeignTestClazz1")
    private static class ForeignTestClazz1 {
        @DatabaseColumn(id = true)
        private int id;
    }

    @DatabaseEntity(name = "ForeignTestClazz2")
    private static class ForeignTestClazz2 {
        @DatabaseColumn(id = true, columnName = "stringId")
        private String stringId;
    }
}