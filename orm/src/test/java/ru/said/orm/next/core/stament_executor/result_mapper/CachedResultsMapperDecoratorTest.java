package ru.said.orm.next.core.stament_executor.result_mapper;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.said.orm.next.core.cache.LRUObjectCache;
import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.stament_executor.CachedStatementExecutorTest;
import ru.said.orm.next.core.stament_executor.DatabaseResults;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.table.TableInfo;

import javax.xml.crypto.Data;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class CachedResultsMapperDecoratorTest {

    @Test
    public void mapResults() throws Exception {
        DataBaseObject<TestClazz> dataBaseObject = createDBObject();
        ResultsMapper<TestClazz> resultsMapper = Mockito.mock(ResultsMapper.class);
        DatabaseResults databaseResults = Mockito.mock(DatabaseResults.class);
        TestClazz testClazz = createTestClazz(1, "Said");
        TestClazz testClazzInCache = createTestClazz(1, "SaidTest");

        dataBaseObject.getObjectCache().get().put(TestClazz.class, 1, testClazzInCache);

        Mockito.when(databaseResults.hasColumn("id")).thenReturn(true);
        Mockito.when(databaseResults.getObject("id")).thenReturn(1);
        Mockito.when(resultsMapper.mapResults(Mockito.any())).thenReturn(testClazz);
        CachedResultsMapperDecorator<TestClazz> cachedResultsMapper = new CachedResultsMapperDecorator<>(dataBaseObject, resultsMapper);

        TestClazz result = cachedResultsMapper.mapResults(databaseResults);

        Assert.assertEquals(1, result.id);
        Assert.assertEquals("SaidTest", result.name);
    }

    private DataBaseObject<TestClazz> createDBObject() throws Exception {
        TableInfo<TestClazz> tableInfo = TableInfo.TableInfoCache.build(TestClazz.class);

        ObjectCache objectCache = new LRUObjectCache(16);

        objectCache.registerClass(TestClazz.class);

        return new DataBaseObject<>(null, tableInfo).caching(true).objectCache(objectCache);
    }

    private TestClazz createTestClazz(Object ... args) throws IllegalAccessException {
        TestClazz clazz = new TestClazz();
        Field[] fields = TestClazz.class.getDeclaredFields();

        for (int i = 0; i < fields.length; ++i) {
            fields[i].setAccessible(true);
            fields[i].set(clazz, args[i]);
            fields[i].setAccessible(false);
        }

        return clazz;
    }

    private static class TestClazz {
        @DBField(id = true)
        private int id;

        @DBField
        private String name;
    }
}