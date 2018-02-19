package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.LRUObjectCache;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.lang.reflect.Field;

public class CachedResultsMapperDecoratorTest {

    @Test
    public void mapResultsFromCache() throws Exception {
        TableInfo<TestClazz> tableInfo = TableInfo.build(TestClazz.class);
        CacheContext cacheContext = createCache();
        ObjectCache objectCache = cacheContext.getObjectCache().get();
        ResultsMapper<TestClazz> resultsMapper = Mockito.mock(ResultsMapper.class);
        DatabaseResults databaseResults = Mockito.mock(DatabaseResults.class);
        TestClazz testClazz = createTestClazz(1, "Said");
        TestClazz testClazzInCache = createTestClazz(1, "SaidTest");

        objectCache.put(TestClazz.class, 1, testClazzInCache);

        Mockito.when(databaseResults.hasColumn("id")).thenReturn(true);
        Mockito.when(databaseResults.getObject("id")).thenReturn(1);
        Mockito.when(resultsMapper.mapResults(Mockito.any())).thenReturn(testClazz);
        CachedResultsMapperDecorator<TestClazz> cachedResultsMapper = new CachedResultsMapperDecorator<>(tableInfo, cacheContext, resultsMapper);

        TestClazz result = cachedResultsMapper.mapResults(databaseResults);

        Assert.assertEquals(1, result.id);
        Assert.assertEquals("SaidTest", result.name);
    }


    private CacheContext createCache() {
        ObjectCache objectCache = new LRUObjectCache(16);

        objectCache.registerClass(TestClazz.class);

        return new CacheContext().caching(TestClazz.class, true).objectCache(objectCache);
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