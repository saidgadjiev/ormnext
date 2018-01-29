package ru.said.up.cache.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by said on 29.01.2018.
 */
public class LRUCacheTest {

    @Test
    public void put() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newLRUCacheBuilder().build();

        Assert.assertEquals(0, cache.size());
        cache.put("test", 1);
        Assert.assertEquals(1, (int) cache.get("test"));
        cache.put("test", 2);
        Assert.assertEquals(2, (int) cache.get("test"));
    }

    @Test
    public void invalidate() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newLRUCacheBuilder().build();

        Assert.assertEquals(0, cache.size());
        cache.put("test", 1);
        Assert.assertEquals(1, (int) cache.get("test"));
        cache.invalidate("test");
        Assert.assertNull(cache.get("test"));
    }

    @Test
    public void invalidateAll() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newLRUCacheBuilder().build();

        Assert.assertEquals(0, cache.size());
        cache.put("test1", 1);
        cache.put("test2", 2);
        Assert.assertEquals(2, cache.size());
        Assert.assertEquals(1, (int) cache.get("test1"));
        Assert.assertEquals(2, (int) cache.get("test2"));
        cache.invalidateAll();
        Assert.assertEquals(0, cache.size());
        Assert.assertNull(cache.get("test1"));
        Assert.assertNull(cache.get("test2"));
    }

    @Test
    public void get() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newLRUCacheBuilder().build();

        Assert.assertEquals(0, cache.size());
        Assert.assertNull(cache.get("test"));
        cache.put("test", 1);
        Assert.assertEquals(1, (int) cache.get("test"));
    }

    @Test
    public void contains() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newLRUCacheBuilder().build();

        Assert.assertEquals(0, cache.size());
        Assert.assertFalse(cache.contains("test"));
        cache.put("test", 1);
        Assert.assertTrue(cache.contains("test"));
    }

    @Test
    public void size() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newLRUCacheBuilder().build();

        Assert.assertEquals(0, cache.size());
        cache.put("test", 1);
        Assert.assertEquals(1, cache.size());
    }

}