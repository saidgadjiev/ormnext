package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.FieldAccessor;
import ru.said.orm.next.core.field.persisters.DataPersister;
import ru.said.orm.next.core.field.persisters.FieldConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DBFieldType implements IDBFieldType {

    private String columnName;

    private DataType dataType;

    private Field field;

    private int length;

    private DataPersister<?> dataPersister;

    private FieldConverter<?> fieldConverter;

    private FieldAccessor fieldAccessor;

    private boolean notNull;

    private boolean id;

    private boolean generated;

    private Object defaultValue;

    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isId() {
        return id;
    }

    @Override
    public boolean isNotNull() {
        return notNull;
    }

    @Override
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return fieldAccessor.access(object);
    }

    @Override
    public DataPersister getDataPersister() {
        return dataPersister;
    }

    @Override
    public void assignId(Object object, Number value) throws IllegalAccessException, InvocationTargetException {
        Object id = dataPersister.convertIdNumber(value);

        fieldAccessor.assign(object, id);
    }

    @Override
    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        fieldAccessor.assign(object, value);
    }

    @Override
    public Field getField() {
        return field;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setDataPersister(DataPersister<?> dataPersister) {
        this.dataPersister = dataPersister;
    }

    public void setFieldConverter(FieldConverter<?> fieldConverter) {
        this.fieldConverter = fieldConverter;
    }

    public void setFieldAccessor(FieldAccessor fieldAccessor) {
        this.fieldAccessor = fieldAccessor;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isDbFieldType() {
        return true;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "columnName='" + columnName + '\'' +
                ", dataType=" + dataType +
                '}';
    }

    public FieldConverter<?> getFieldConverter() {
        return fieldConverter;
    }
}
