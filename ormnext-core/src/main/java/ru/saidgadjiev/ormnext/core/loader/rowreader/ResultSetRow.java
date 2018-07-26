package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.table.internal.alias.EntityAliases;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by said on 23.07.2018.
 */
public class ResultSetRow {

    private Map<String, ResultSetValue> values = new LinkedHashMap<>();

    private Map<EntityAliases, Map<String, ResultSetValue>> aliasesValues = new LinkedHashMap<>();

    public void add(String alias, ResultSetValue value) {
        values.put(alias, value);
    }

    public ResultSetValue get(String alias) {
        return values.get(alias);
    }

    public void addAll(Map<String , ResultSetValue> values) {
        this.values.putAll(values);
    }

    public void addValues(EntityAliases aliases, Map<String, ResultSetValue> values) {
        aliasesValues.put(aliases, values);
    }

    public Map<String, ResultSetValue> getValues(EntityAliases entityAliases) {
        return aliasesValues.get(entityAliases);
    }
}
