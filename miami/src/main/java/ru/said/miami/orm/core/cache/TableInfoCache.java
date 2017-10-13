package ru.said.miami.orm.core.cache;

import javafx.scene.control.Tab;
import ru.said.miami.orm.core.cache.core.Cache;
import ru.said.miami.orm.core.cache.core.CacheManager;
import ru.said.miami.orm.core.table.TableInfo;

public class TableInfoCache {

    private Cache<Class<?>, TableInfo<?>> cache = CacheManager.buildCache();
    private static final TableInfoCache INSTANCE = new TableInfoCache();

    public static TableInfoCache getInstance() {
        return INSTANCE;
    }

    public void add(Class<?> key, TableInfo<?> value) {
        cache.put(key, value);
    }

    public TableInfo<?> get(Class<?> key) {
        return cache.get(key);
    }

    public void remove(Class<?> key) {
        cache.remove(key);
    }
}
