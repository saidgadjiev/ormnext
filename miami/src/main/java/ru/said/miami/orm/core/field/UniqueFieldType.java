package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.Unique;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 30.10.17.
 */
public class UniqueFieldType {

    private List<DBFieldType> dbFieldTypes = new ArrayList<>();

    public List<DBFieldType> getDbFieldTypes() {
        return dbFieldTypes;
    }

    public static<T> UniqueFieldType build(Unique unique, Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        UniqueFieldType uniqueFieldType = new UniqueFieldType();

        for (String columnName: unique.columns()) {
            uniqueFieldType.dbFieldTypes.add(DBFieldType.build(findFieldByName(columnName, tClass)));
        }

        return uniqueFieldType;
    }

    private static Field findFieldByName(String foreignFieldName, Class<?> clazz) throws NoSuchFieldException {
        return clazz.getDeclaredField(foreignFieldName);
    }

}
