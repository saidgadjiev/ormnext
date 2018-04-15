package ru.saidgadjiev.orm.next.core.field.field_type;

import org.junit.Assert;
import org.junit.Test;
import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.ForeignCollectionField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForeignCollectionFieldTypeTest {

    @Test
    public void getField() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignCollectionFieldType fieldType = (ForeignCollectionFieldType) new ForeignCollectionColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(field, fieldType.getField());
    }

    @Test
    public void add() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignCollectionFieldType fieldType = (ForeignCollectionFieldType) new ForeignCollectionColumnTypeFactory().createFieldType(field);
        TestClazz testClazz = new TestClazz();
        ForeignCollectionTestClazz foreignCollectionTestClazz = new ForeignCollectionTestClazz();

        foreignCollectionTestClazz.id = 1;
        fieldType.add(testClazz, foreignCollectionTestClazz);
        Assert.assertEquals(1, testClazz.field1.size());
        Assert.assertEquals(foreignCollectionTestClazz, testClazz.field1.get(0));
    }

    @Test
    public void addAll() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignCollectionFieldType fieldType = (ForeignCollectionFieldType) new ForeignCollectionColumnTypeFactory().createFieldType(field);
        TestClazz testClazz = new TestClazz();
        ForeignCollectionTestClazz foreignCollectionTestClazz = new ForeignCollectionTestClazz();

        foreignCollectionTestClazz.id = 1;
        fieldType.addAll(testClazz, Collections.singletonList(foreignCollectionTestClazz));
        Assert.assertEquals(1, testClazz.field1.size());
        Assert.assertEquals(foreignCollectionTestClazz, testClazz.field1.get(0));
    }

    @Test
    public void getForeignField() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        Field foreignField = ForeignCollectionTestClazz.class.getDeclaredFields()[1];
        ForeignCollectionFieldType fieldType = (ForeignCollectionFieldType) new ForeignCollectionColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(foreignField, fieldType.getForeignField());
    }

    @Test
    public void getForeignFieldClass() throws Exception {
        Field field = TestClazz.class.getDeclaredFields()[0];
        ForeignCollectionFieldType fieldType = (ForeignCollectionFieldType) new ForeignCollectionColumnTypeFactory().createFieldType(field);

        Assert.assertEquals(ForeignCollectionTestClazz.class, fieldType.getForeignFieldClass());
    }

    public static class TestClazz {
        @ForeignCollectionField
        private List<ForeignCollectionTestClazz> field1 = new ArrayList<>();

    }

    public static class ForeignCollectionTestClazz {
        @DatabaseColumn(id = true)
        private int id;

        @DatabaseColumn
        private TestClazz testClazz;
    }
}