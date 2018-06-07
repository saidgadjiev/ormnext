package ru.saidgadjiev.ormnext.core.table.internal.instatiator;

/**
 * Interface for implement entity instantiator.
 *
 * @author Said Gadjiev
 */
public interface Instantiator {

    /**
     * Create a new instance.
     * @return a new instance
     */
    Object instantiate();
}
