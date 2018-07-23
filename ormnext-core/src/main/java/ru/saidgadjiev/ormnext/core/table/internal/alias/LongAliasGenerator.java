package ru.saidgadjiev.ormnext.core.table.internal.alias;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Use for create unique alias for value.
 *
 * @author Said Gadjiev
 */
public class LongAliasGenerator implements AliasGenerator {

    /**
     * Map for save last added alias suffix for value.
     */
    private Map<String, AtomicInteger> aliasSuffixesMap = new HashMap<>();

    @Override
    public synchronized String generate(String name) {
        aliasSuffixesMap.putIfAbsent(name, new AtomicInteger());

        return name + "_" + aliasSuffixesMap.get(name).getAndIncrement();
    }
}
