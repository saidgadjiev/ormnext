package ru.saidgadjiev.ormnext.core.loader.rowreader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by said on 23.07.2018.
 */
public class ResultSetRow {

    private Map<String, ResultSetValue> values = new LinkedHashMap<>();

    public void add(String alias, ResultSetValue value) {
        values.put(alias, value);
    }

    public ResultSetValue get(String alias) {
        return values.get(alias);
    }

    public void addAll(Map<String , ResultSetValue> values) {
        this.values.putAll(values);
    }
}
