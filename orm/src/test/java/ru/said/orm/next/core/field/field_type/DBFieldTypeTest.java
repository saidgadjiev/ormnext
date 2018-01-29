package ru.said.orm.next.core.field.field_type;

import org.junit.Assert;
import org.junit.Test;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.persisters.DataPersister;
import ru.said.orm.next.core.field.persisters.IntegerDataPersister;

import java.lang.reflect.Field;

public class DBFieldTypeTest {

    @Test
    public void testIsId() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertTrue(fieldType.isId());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertFalse(fieldType.isId());
    }

    @Test
    public void testIsNotNull() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertTrue(fieldType.isNotNull());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertFalse(fieldType.isNotNull());
    }

    @Test
    public void testIsGeneratedd() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertTrue(fieldType.isGenerated());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertFalse(fieldType.isGenerated());
    }

    @Test
    public void testGetColumnName() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals("test1", fieldType.getColumnName());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals("test2", fieldType.getColumnName());
        field = TestClazz.class.getDeclaredFields()[2];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals("field3", fieldType.getColumnName());
    }

    @Test
    public void testGetDataType() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(DataType.INTEGER, fieldType.getDataType());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(DataType.STRING, fieldType.getDataType());
        field = TestClazz.class.getDeclaredFields()[2];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(DataType.INTEGER, fieldType.getDataType());
    }

    @Test
    public void testAssign() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);
        TestClazz table = new TestClazz();

        fieldType.assign(table, 1);
        Assert.assertEquals(1, table.field1);
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        fieldType.assign(table, "3");
        Assert.assertEquals("3", table.field2);
    }

    @Test
    public void testGetDataPersister() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);
        DataPersister dataPersister = fieldType.getDataPersister();

        Assert.assertTrue(dataPersister instanceof IntegerDataPersister);
    }

    @Test
    public void testAsccess() throws Exception {
        Field field = TestAccess.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);
        TestAccess access = new TestAccess();

        access.field1 = 2;
        Assert.assertEquals(2, fieldType.access(access));
        field = TestAccess.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        access.field2 = "3";
        Assert.assertEquals("3", fieldType.access(access));
    }

    @Test
    public void testGetLength() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(50, fieldType.getLength());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(128, fieldType.getLength());
    }

    @Test
    public void testGetField() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(field, fieldType.getField());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(field, fieldType.getField());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        DBFieldType fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals(5, fieldType.getDefaultValue());
        field = TestClazz.class.getDeclaredFields()[1];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertEquals("test", fieldType.getDefaultValue());
        field = TestClazz.class.getDeclaredFields()[2];
        fieldType = (DBFieldType) new DBFieldTypeFactory().createFieldType(field);

        Assert.assertNull(fieldType.getDefaultValue());
    }

    public static class TestClazz {
        @DBField(id = true, generated = true, notNull = true, columnName = "test1", dataType = DataType.INTEGER, length = 50, defaultValue = "5")
        private int field1;

        @DBField(id = false, generated = false, notNull = false, columnName = "test2", dataType = DataType.STRING, length = 128, defaultValue = "test")
        private String field2;

        @DBField
        private int field3;
    }

    public static class TestAccess {
        @DBField
        private int field1;

        @DBField
        private String field2;
    }
}