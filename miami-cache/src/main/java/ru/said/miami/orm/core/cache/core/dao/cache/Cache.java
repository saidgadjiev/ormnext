package ru.said.miami.orm.core.cache.core.dao.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by said on 08.05.17.
 */
public class Cache {

    private final Map<String, CacheResultSet> cache = new HashMap<>();

    public void add(String sql, CacheResultSet cacheData) {
        cache.put(sql, cacheData);
    }

    public CacheResultSet get(String sql) {
        return cache.get(sql);
    }

    public boolean has(String sql) {
        return cache.containsKey(sql);
    }
}
