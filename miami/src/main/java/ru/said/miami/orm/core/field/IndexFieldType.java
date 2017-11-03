package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.Index;

import java.util.ArrayList;
import java.util.List;

public class IndexFieldType {

    private List<DBFieldType> dbFieldTypes;

    private String name;

    private IndexFieldType(String name, List<DBFieldType> dbFieldTypes) {
        this.dbFieldTypes = dbFieldTypes;
        this.name = name;
    }

    public List<DBFieldType> getDbFieldTypes() {
        return dbFieldTypes;
    }

    public String getName() {
        return name;
    }

    public static<T> IndexFieldType build(Index index, Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        List<DBFieldType> indexFieldTypes = new ArrayList<>();

        for (String columnName: index.columns()) {
            indexFieldTypes.add(DBFieldType.DBFieldTypeCache.build(tClass.getDeclaredField(columnName)));
        }

        return new IndexFieldType(index.name(), indexFieldTypes);
    }
}
