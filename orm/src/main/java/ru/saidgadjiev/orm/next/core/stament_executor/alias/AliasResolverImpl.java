package ru.saidgadjiev.orm.next.core.stament_executor.alias;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AliasResolverImpl implements AliasResolver {

    private Map<String, AtomicInteger> aliasSuffixesMap = new HashMap<>();

    public synchronized String createAlias(String value) {
        aliasSuffixesMap.putIfAbsent(value, new AtomicInteger());

        return value + "_" + aliasSuffixesMap.get(value).incrementAndGet();
    }
}
