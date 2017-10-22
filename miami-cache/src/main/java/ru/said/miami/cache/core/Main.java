package ru.said.miami.cache.core;

import ru.said.miami.cache.core.cuncurrent.LRUCacheMap;

public class Main {

    public static void main(String[] args) {
        LRUCacheMap<Integer, String> cache = new LRUCacheMap<>(5);

        for (int i = 0; i < 5; ++i) {
            cache.put(i, "test" + i);
        }

        System.out.println("cache = " + cache.get(0));
        System.out.println("cache = " + cache.put(5, "test5"));
    }
}
