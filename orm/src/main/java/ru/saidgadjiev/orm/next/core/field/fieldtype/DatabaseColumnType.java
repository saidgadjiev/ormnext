package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;
import ru.saidgadjiev.orm.next.core.table.internal.visitor.EntityMetadataVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DatabaseColumnType implements IDatabaseColumnType {

    private String columnName;

    private int dataType;

    private Field field;

    private int length;

    private DataPersister<?> dataPersister;

    private FieldAccessor fieldAccessor;

    private boolean notNull;

    private boolean id;

    private boolean generated;

    private Object defaultValue;

    private String format;

    private String tableName;

    @Override
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
    public int getDataType() {
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
    public void assign(Object object, Object value) {
        try {
            fieldAccessor.assign(object, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setDataType(int dataType) {
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
    public String getOwnerTableName() {
        return tableName;
    }

    public void setOwnerTableName(String ownerTableName) {
        this.tableName = ownerTableName;
    }

    @Override
    public String toString() {
        return "FieldType{" +
                "columnName='" + columnName + '\'' +
                ", dataType=" + dataType +
                '}';
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {

    }
}
