package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AliasResolverImpl implements AliasResolver {

    private Map<String, AtomicInteger> aliasSuffixesMap = new HashMap<>();

    @Override
    public synchronized String createAlias(String value) {
        aliasSuffixesMap.putIfAbsent(value, new AtomicInteger());

        return value + "_" + aliasSuffixesMap.get(value).getAndIncrement();
    }
}
