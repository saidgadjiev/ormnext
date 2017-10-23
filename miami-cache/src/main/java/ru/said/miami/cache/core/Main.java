package ru.said.miami.cache.core;

import ru.said.miami.cache.core.cuncurrent.LRUCacheHashMap;

public class Main {

    public static void main(String[] args) {
        LRUCacheHashMap<Integer, String> cache = new LRUCacheHashMap<>(5);

        for (int i = 0; i < 5; ++i) {
            cache.put(i, "test" + i);
        }

        System.out.println("cache = " + cache.get(0));
        System.out.println("cache = " + cache.put(5, "test5"));
    }
}
