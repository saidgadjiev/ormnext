package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.table.Index;
import ru.said.orm.next.core.table.utils.TableInfoUtils;

import java.util.ArrayList;
import java.util.List;

public class IndexFieldType {

    private List<DBFieldType> dbFieldTypes;

    private String name;

    private boolean unique;

    private String tableName;

    private IndexFieldType(String name, boolean unique, String tableName, List<DBFieldType> dbFieldTypes) {
        this.dbFieldTypes = dbFieldTypes;
        this.name = name;
        this.tableName = tableName;
        this.unique = unique;
    }

    public List<DBFieldType> getDbFieldTypes() {
        return dbFieldTypes;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isUnique() {
        return unique;
    }

    public static<T> IndexFieldType build(Index index, Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        List<DBFieldType> indexFieldTypes = new ArrayList<>();

        for (String columnName: index.columns()) {
            indexFieldTypes.add(DBFieldType.DBFieldTypeCache.build(tClass.getDeclaredField(columnName)));
        }

        return new IndexFieldType(index.name(), index.unique(), TableInfoUtils.resolveTableName(tClass), indexFieldTypes);
    }
}
