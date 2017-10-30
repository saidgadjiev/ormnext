package ru.said.miami.orm.core.field;

import ru.said.miami.orm.core.table.DBTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by said on 30.10.17.
 */
public class PrimaryKeyFieldType {

    private DBFieldType dbFieldType;

    private GeneratedAlgorithm generatedAlgorithm;

    private static Field findFieldByName(String foreignFieldName, Class<?> clazz) throws NoSuchFieldException {
        return clazz.getDeclaredField(foreignFieldName);
    }

    public DBFieldType getDbFieldType() {
        return dbFieldType;
    }

    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return dbFieldType.access(object);
    }

    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        dbFieldType.assign(object, value);
    }

    public GeneratedAlgorithm getGeneratedAlgorithm() {
        return generatedAlgorithm;
    }

    public String getColumnName() {
        return dbFieldType.getColumnName();
    }

    public DataType getDataType() {
        return dbFieldType.getDataType();
    }

    public static<T> PrimaryKeyFieldType build(String columnName, Class<T> clazz) throws NoSuchFieldException, NoSuchMethodException  {
        PrimaryKeyFieldType primaryKeyFieldType = new PrimaryKeyFieldType();
        Field field = findFieldByName(columnName, clazz);

        primaryKeyFieldType.dbFieldType = DBFieldType.DBFieldTypeCache.build(field);
        GeneratedValue dbField = field.getAnnotation(GeneratedValue.class);

        primaryKeyFieldType.generatedAlgorithm = dbField.value();

        return primaryKeyFieldType;
    }
}
