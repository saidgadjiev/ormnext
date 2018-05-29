package ru.saidgadjiev.ormnext.core.table.internal.alias;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unique uid generator.
 */
public class UIDGenerator {

    /**
     * Use for generate next unique integer.
     */
    private AtomicInteger generator = new AtomicInteger();

    /**
     * Generate next unique uid. It will be "uid:" + unique integer from {@link #generator}.
     * @return unique next uid
     */
    public synchronized String nextUID() {
        return "uid:" + generator.getAndIncrement();
    }
}
