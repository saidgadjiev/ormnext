package ru.said.miami.orm.core.cache.core;

import ru.said.miami.orm.core.cache.core.cuncurrent.LRUCache;

public class Main {

    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(5);

        for (int i = 0; i < 5; ++i) {
            cache.put(i, "test" + i);
        }

        System.out.println("cache = " + cache.get(0));
        System.out.println("cache = " + cache.put(5, "test5"));
    }
}
