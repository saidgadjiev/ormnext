package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.*;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.instatiator.ObjectInstantiator;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.validator.datapersister.GeneratedTypeValidator;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private String defaultDefinition;

    private String tableName;

    private static final Map<Field, DatabaseColumnType> CACHE = new HashMap<>();

    private Converter<?, ?> converter;

    @Override
    public String getDefaultDefinition() {
        return defaultDefinition;
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
    public Object access(Object object) throws SQLException {
        try {
            return fieldAccessor.access(object);
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public DataPersister getDataPersister() {
        return dataPersister;
    }

    @Override
    public void assignId(Object object, Number value) throws SQLException {
        try {
            fieldAccessor.assign(object, value);
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void assign(Object object, Object value) throws SQLException {
        try {
            fieldAccessor.assign(object, value);
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public Field getField() {
        return field;
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

    public void setDefaultDefinition(String defaultDefinition) {
        this.defaultDefinition = defaultDefinition;
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
    public Optional<Converter<?, ?>> getConverter() {
        return Optional.ofNullable(converter);
    }

    public static DatabaseColumnType build(Field field) {
        if (!field.isAnnotationPresent(DatabaseColumn.class)) {
            return null;
        }
        if (CACHE.containsKey(field)) {
            return CACHE.get(field);
        }
        DatabaseColumn databaseColumn = field.getAnnotation(DatabaseColumn.class);
        DatabaseColumnType fieldType = new DatabaseColumnType();

        fieldType.setField(field);
        fieldType.setColumnName(databaseColumn.columnName().isEmpty() ? field.getName().toLowerCase() : databaseColumn.columnName());
        fieldType.setLength(databaseColumn.length());
        fieldType.setFieldAccessor(new FieldAccessor(field));

        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            Integer dataType = databaseColumn.dataType();
            DataPersister<?> dataPersister = dataType.equals(DataType.UNKNOWN) ? DataPersisterManager.lookup(field.getType()) : DataPersisterManager.lookup(dataType);

            new GeneratedTypeValidator(field, databaseColumn.generated()).validate(dataPersister);
            fieldType.setDataPersister(dataPersister);
            fieldType.setDataType(dataType.equals(DataType.UNKNOWN) ? dataPersister.getDataType() : dataType);
            String defaultDefinition = databaseColumn.defaultDefinition();

            if (!defaultDefinition.equals(DatabaseColumn.DEFAULT_STR)) {
                fieldType.setDefaultDefinition(defaultDefinition);
            }
        }
        if (field.isAnnotationPresent(ru.saidgadjiev.ormnext.core.field.Converter.class)) {
            ru.saidgadjiev.ormnext.core.field.Converter converterAnnotation = field.getAnnotation(ru.saidgadjiev.ormnext.core.field.Converter.class);
            Class<?> converterClass = converterAnnotation.value();

            fieldType.converter = (Converter<?, ?>) new ObjectInstantiator(converterClass).instantiate();
        }
        fieldType.setNotNull(databaseColumn.notNull());
        fieldType.setId(databaseColumn.id());
        fieldType.setGenerated(databaseColumn.generated());

        CACHE.put(field, fieldType);

        return fieldType;
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
