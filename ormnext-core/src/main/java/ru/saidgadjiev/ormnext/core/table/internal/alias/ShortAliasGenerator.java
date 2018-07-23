package ru.saidgadjiev.ormnext.core.table.internal.alias;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Use for create unique aliases.
 *
 * @author Said Gadjiev
 */
public class ShortAliasGenerator implements AliasGenerator {

    /**
     * Restricted combinations.
     */
    private static final Collection<String> RESTRICTED = new HashSet<>();

    /**
     * Alias generate keys.
     */
    private static final String VALUES = "abcdeghijklmnopqrsuvwxyz";

    static {
        RESTRICTED.add("by");
    }

    /**
     * Unique counter.
     */
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public String generate(String value) {
        long val = counter.getAndIncrement();

        StringBuilder name = new StringBuilder();
        do {
            long mod = val % VALUES.length();
            name.insert(0, VALUES.charAt((int) mod));
            val = val / VALUES.length();
        } while (val > 0);

        if (RESTRICTED.contains(name.toString())) {
            return generate(value);
        }

        return name.toString();
    }
}
