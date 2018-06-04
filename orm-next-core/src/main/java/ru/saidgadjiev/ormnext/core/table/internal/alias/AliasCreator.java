package ru.saidgadjiev.ormnext.core.table.internal.alias;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Use for create unique alias for value.
 *
 * @author said gadjiev
 */
public class AliasCreator {

    /**
     * Map for save last added alias suffix for value.
     */
    private Map<String, AtomicInteger> aliasSuffixesMap = new HashMap<>();

    /**
     * Create alias by value. It will be value + unique suffix from {@link #aliasSuffixesMap}.
     * @param value target value
     * @return unique value alias
     */
    public synchronized String createAlias(String value) {
        aliasSuffixesMap.putIfAbsent(value, new AtomicInteger());

        return value + "_" + aliasSuffixesMap.get(value).getAndIncrement();
    }
}
