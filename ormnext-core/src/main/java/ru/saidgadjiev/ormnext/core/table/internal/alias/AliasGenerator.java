package ru.saidgadjiev.ormnext.core.table.internal.alias;

/**
 * Interface for implement alias generator.
 *
 * @author Said Gadjiev
 */
public interface AliasGenerator {

    /**
     * Generate a new alias.
     *
     * @param name target name
     * @return a new alias
     */
    String generate(String name);

}
