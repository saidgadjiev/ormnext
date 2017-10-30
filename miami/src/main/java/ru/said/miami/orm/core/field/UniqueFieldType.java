package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.DBTable;
import ru.said.miami.orm.core.table.Unique;

/**
 * Created by said on 30.10.17.
 */
public class UniqueFieldType {

    private String[] uniques;

    public static<T> UniqueFieldType build(Unique unique) {
        UniqueFieldType uniqueFieldType = new UniqueFieldType();

        uniqueFieldType.uniques = unique.columns();

        return uniqueFieldType;
    }
}
