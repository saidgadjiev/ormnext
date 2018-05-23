package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.exception.ConverterInstantiateException;
import ru.saidgadjiev.ormnext.core.field.*;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

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

    private boolean insertable;

    private boolean updatable;

    private static final Map<Field, DatabaseColumnType> CACHE = new HashMap<>();

    private List<ru.saidgadjiev.ormnext.core.field.persister.Converter<?, Object>> converters;

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
    public void assign(Object object, Object value) {
        fieldAccessor.assign(object, value);
    }

    @Override
    public Field getField() {
        return field;
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

    @Override
    public Optional<List<ru.saidgadjiev.ormnext.core.field.persister.Converter<?, Object>>> getConverters() {
        return Optional.ofNullable(converters);
    }

    @Override
    public boolean insertable() {
        return insertable;
    }

    @Override
    public boolean updatable() {
        return updatable;
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

        fieldType.field = field;
        fieldType.columnName = databaseColumn.columnName().isEmpty() ? field.getName().toLowerCase() : databaseColumn.columnName();
        fieldType.length = databaseColumn.length();
        fieldType.fieldAccessor = new FieldAccessor(field);

        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            Integer dataType = databaseColumn.dataType();
            DataPersister<?> dataPersister = dataType.equals(DataType.UNKNOWN) ? DataPersisterManager.lookup(field.getType()) : DataPersisterManager.lookup(dataType);

            fieldType.tableName = DatabaseEntityMetadataUtils.resolveTableName(field.getDeclaringClass());
            fieldType.dataPersister = dataPersister;
            fieldType.dataType = dataType.equals(DataType.UNKNOWN) ? dataPersister.getDataType() : dataType;
            String defaultDefinition = databaseColumn.defaultDefinition();

            if (!defaultDefinition.equals(DatabaseColumn.DEFAULT_STR)) {
                fieldType.defaultDefinition = defaultDefinition;
            }
        }
        if (field.isAnnotationPresent(ConverterGroup.class)) {
            ConverterGroup converterGroupAnnotation = field.getAnnotation(ConverterGroup.class);

            fieldType.converters = new ArrayList<>();
            for (ru.saidgadjiev.ormnext.core.field.Converter converter: converterGroupAnnotation.converters()) {
                fieldType.converters.add(toConverter(converter));
            }
        } else if (field.isAnnotationPresent(ru.saidgadjiev.ormnext.core.field.Converter.class)) {
            ru.saidgadjiev.ormnext.core.field.Converter converterAnnotation = field.getAnnotation(ru.saidgadjiev.ormnext.core.field.Converter.class);

            fieldType.converters = new ArrayList<>();
            fieldType.converters.add(toConverter(converterAnnotation));
        }
        fieldType.notNull = databaseColumn.notNull();
        fieldType.id = databaseColumn.id();
        fieldType.generated = databaseColumn.generated();
        fieldType.insertable = databaseColumn.insertable();
        fieldType.updatable = databaseColumn.updatable();

        CACHE.put(field, fieldType);

        return fieldType;
    }

    private static ru.saidgadjiev.ormnext.core.field.persister.Converter<?, Object> toConverter(ru.saidgadjiev.ormnext.core.field.Converter converter) {
        try {
            List<Class<?>> parametrTypes = new ArrayList<>();

            for (int i = 0; i < converter.args().length; ++i) {
                parametrTypes.add(String.class);
            }
            Constructor<? extends ru.saidgadjiev.ormnext.core.field.persister.Converter> constructor = converter.value().getDeclaredConstructor(parametrTypes.toArray(new Class[parametrTypes.size()]));

            return constructor.newInstance(converter.args());
        } catch (Exception ex) {
            throw new ConverterInstantiateException(converter.value(), ex);
        }
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
