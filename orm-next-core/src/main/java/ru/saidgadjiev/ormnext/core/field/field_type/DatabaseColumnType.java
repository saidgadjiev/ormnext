package ru.saidgadjiev.ormnext.core.field.field_type;

import ru.saidgadjiev.ormnext.core.exception.InstantiationException;
import ru.saidgadjiev.ormnext.core.field.Converter;
import ru.saidgadjiev.ormnext.core.field.ConverterGroup;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.FieldAccessor;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.field.data_persister.ColumnConverter;
import ru.saidgadjiev.ormnext.core.field.data_persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class represent simple database column annotated by {@link DatabaseColumn}.
 */
public final class DatabaseColumnType extends BaseDatabaseColumnType {

    /**
     * Column name.
     */
    private String columnName;

    /**
     * Data type. It will be any integer value.
     * @see DataType
     */
    private int dataType;

    /**
     * Column field.
     */
    private Field field;

    /**
     * Column length.
     */
    private int length;

    /**
     * Column data persister.
     * @see DataPersister
     */
    private DataPersister dataPersister;

    /**
     * Helper for field access.
     */
    private FieldAccessor fieldAccessor;

    /**
     * Not null.
     */
    private boolean notNull;

    /**
     * Is id.
     */
    private boolean id;

    /**
     * Is generated.
     */
    private boolean generated;

    /**
     * Default definition.
     */
    private String defaultDefinition;

    /**
     * This column owner table name.
     */
    private String tableName;

    /**
     * Is insertable.
     */
    private boolean insertable;

    /**
     * Is updatable.
     */
    private boolean updatable;

    /**
     * Default if null.
     */
    private boolean defaultIfNull;

    /**
     * Field column converters. It use for convert java to sql value and sql to java.
     */
    private List<ColumnConverter<?, Object>> columnConverters;

    /**
     * Create a new instance only from {@link #build(Field)} method.
     */
    private DatabaseColumnType() { }

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
    public Object access(Object object) {
        return fieldAccessor.access(object);
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
    public boolean isDbColumnType() {
        return true;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public Optional<List<ColumnConverter<?, Object>>> getColumnConverters() {
        return Optional.ofNullable(columnConverters);
    }

    @Override
    public boolean insertable() {
        return insertable;
    }

    @Override
    public boolean updatable() {
        return updatable;
    }

    @Override
    public boolean defaultIfNull() {
        return super.defaultIfNull();
    }

    /**
     * Method for build new instance by field.
     * @param field target field
     * @return new instance
     */
    public static DatabaseColumnType build(Field field) {
        if (!field.isAnnotationPresent(DatabaseColumn.class)) {
            return null;
        }
        DatabaseColumn databaseColumn = field.getAnnotation(DatabaseColumn.class);
        DatabaseColumnType fieldType = new DatabaseColumnType();

        fieldType.field = field;
        fieldType.columnName = databaseColumn.columnName().isEmpty()
                ? field.getName().toLowerCase() : databaseColumn.columnName();
        fieldType.length = databaseColumn.length();
        fieldType.fieldAccessor = new FieldAccessor(field);

        if (!field.isAnnotationPresent(ForeignColumn.class)) {
            Integer dataType = databaseColumn.dataType();
            DataPersister dataPersister = dataType.equals(DataType.UNKNOWN)
                    ? DataPersisterManager.lookup(field.getType()) : DataPersisterManager.lookup(dataType);

            fieldType.tableName = DatabaseEntityMetadataUtils.resolveTableName(field.getDeclaringClass());
            fieldType.dataPersister = dataPersister;
            fieldType.dataType = dataType.equals(DataType.UNKNOWN) ? dataPersister.getDataType() : dataType;
            String defaultDefinition = databaseColumn.defaultDefinition();

            if (!defaultDefinition.isEmpty()) {
                fieldType.defaultDefinition = defaultDefinition;
            }
        }
        if (field.isAnnotationPresent(ConverterGroup.class)) {
            ConverterGroup converterGroupAnnotation = field.getAnnotation(ConverterGroup.class);

            fieldType.columnConverters = new ArrayList<>();
            for (Converter converter : converterGroupAnnotation.converters()) {
                fieldType.columnConverters.add(toConverter(converter));
            }
        } else if (field.isAnnotationPresent(Converter.class)) {
            Converter converterAnnotation = field.getAnnotation(Converter.class);

            fieldType.columnConverters = new ArrayList<>();
            fieldType.columnConverters.add(toConverter(converterAnnotation));
        }
        fieldType.notNull = databaseColumn.notNull();
        fieldType.id = databaseColumn.id();
        fieldType.generated = databaseColumn.generated();
        fieldType.insertable = databaseColumn.insertable();
        fieldType.updatable = databaseColumn.updatable();
        fieldType.defaultIfNull = databaseColumn.defaultIfNull();

        return fieldType;
    }

    /**
     * Make {@link ColumnConverter} from {@link Converter}.
     * @param converter target converter annotation
     * @return column converter instance which created from converter annotation
     */
    private static ColumnConverter<?, Object> toConverter(Converter converter) {
        try {
            List<Class<?>> parametrTypes = new ArrayList<>();

            for (int i = 0; i < converter.args().length; ++i) {
                parametrTypes.add(String.class);
            }
            Constructor<? extends ColumnConverter> constructor =
                    converter.value().getDeclaredConstructor(parametrTypes.toArray(new Class[parametrTypes.size()]));

            return constructor.newInstance(converter.args());
        } catch (Exception ex) {
            throw new InstantiationException(
                    "Converter " + converter.value() + " instantiate exception. " + ex.getMessage(),
                    converter.value(),
                    ex
            );
        }
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
    }
}
