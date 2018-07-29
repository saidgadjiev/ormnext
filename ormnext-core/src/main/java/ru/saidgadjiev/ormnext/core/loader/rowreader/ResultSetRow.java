package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Result set row.
 *
 * @author Said Gadjiev
 */
public class ResultSetRow {

    /**
     * Read from result set values.
     */
    private Map<String, ResultSetValue> values = new LinkedHashMap<>();

    /**
     * Read from result set values by aliases.
     */
    private Map<EntityAliases, Map<String, ResultSetValue>> aliasesValues = new LinkedHashMap<>();

    /**
     * Add new read value.
     *
     * @param alias target alias
     * @param value target value
     */
    public void add(String alias, ResultSetValue value) {
        values.put(alias, value);
    }

    /**
     * Retrieve read value.
     *
     * @param alias target alias
     * @return read value
     */
    public ResultSetValue get(String alias) {
        return values.get(alias);
    }

    /**
     * Add all read result set values.
     *
     * @param values target values
     */
    public void addAll(Map<String, ResultSetValue> values) {
        this.values.putAll(values);
    }

    /**
     * Add read values by entity aliases.
     *
     * @param aliases target entity aliases
     * @param values target values
     */
    public void addValues(EntityAliases aliases, Map<String, ResultSetValue> values) {
        aliasesValues.put(aliases, values);
    }

    /**
     * Retrieve values by entity aliases.
     *
     * @param entityAliases target entity aliases.
     * @return values
     */
    public Map<String, ResultSetValue> getValues(EntityAliases entityAliases) {
        return aliasesValues.get(entityAliases);
    }
}
