package ru.said.miami.orm.core.cache.core.clause.query;

/**
 * Created by said on 24.06.17.
 */
public class WhereValue {
    private TYPE type;
    private String name;
    private String value;


    public WhereValue(TYPE type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public enum TYPE {
        MORE,
        LESS,
        BETWEEN
    }
}
