package ru.saidgadjiev.ormnext.core.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AliasResolver {

    private Map<String, AtomicInteger> aliasSuffixesMap = new HashMap<>();

    public synchronized String createAlias(String value) {
        aliasSuffixesMap.putIfAbsent(value, new AtomicInteger());

        return value + "_" + aliasSuffixesMap.get(value).getAndIncrement();
    }
}
