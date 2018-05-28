package ru.saidgadjiev.ormnext.core.field.field_type;

import ru.saidgadjiev.ormnext.core.field.*;
import ru.saidgadjiev.ormnext.core.field.data_persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;
import ru.saidgadjiev.ormnext.core.utils.FieldTypeUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class represent column type annotated with {@link ForeignCollectionField}.
 */
public class ForeignCollectionColumnType extends BaseDatabaseColumnType implements IForeignDatabaseColumnType {

    /**
     * Current field which annotated by {@link ForeignCollectionField}.
     */
    private Field field;

    /**
     * Field refers to this field.
     */
    private Field foreignField;

    /**
     * Collection object class.
     */
    private Class<?> collectionObjectClass;

    /**
     * Collection fetch type.
     * @see FetchType
     */
    private FetchType fetchType;

    /**
     * Collection type. Use for decorating collection by our lazy collection.
     * @see CollectionType
     */
    private CollectionType collectionType;

    /**
     * Field accessor.
     */
    private FieldAccessor fieldAccessor;

    /**
     * Owner table name.
     */
    private String tableName;

    /**
     * Column name refers to this column.
     */
    private String foreignColumnName;

    /**
     * Foreign table name.
     */
    private String foreignTableName;

    /**
     * Column key.
     * @see ColumnKey
     */
    private ColumnKey columnKey;

    @Override
    public Object access(Object object) {
        return fieldAccessor.access(object);
    }

    @Override
    public DataPersister getDataPersister() {
        throw new UnsupportedOperationException();
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
    public boolean isForeignCollectionColumnType() {
        return true;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean insertable() {
        return false;
    }

    @Override
    public boolean updatable() {
        return false;
    }

    /**
     * Add new value to collection in requested object.
     * @param object target object
     * @param value target value
     */
    public void add(Object object, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
                ((Collection<Object>) field.get(object)).add(value);
                field.setAccessible(false);
            } else {
                ((Collection<Object>) field.get(object)).add(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Return collection object class.
     * @return collection object class
     */
    public Class<?> getCollectionObjectClass() {
        return collectionObjectClass;
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }

    /**
     * Return collection type.
     * @return collection type
     * @see CollectionType
     */
    public CollectionType getCollectionType() {
        return collectionType;
    }

    @Override
    public ColumnKey getColumnKey() {
        if (columnKey == null) {
            columnKey = new ColumnKey(foreignTableName, foreignColumnName);
        }

        return columnKey;
    }

    @Override
    public String getForeignTableName() {
        return foreignTableName;
    }

    @Override
    public String getForeignColumnName() {
        return foreignColumnName;
    }

    /**
     * Field refers to this.
     * @return foreign field
     */
    public Field getForeignField() {
        return foreignField;
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }

    /**
     * Method for build new instance by field.
     * @param field target field
     * @return new instance
     */
    public static ForeignCollectionColumnType build(Field field) {
        if (!field.isAnnotationPresent(ForeignCollectionField.class)) {
            return null;
        }
        ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
        ForeignCollectionColumnType fieldType = new ForeignCollectionColumnType();
        String foreignFieldName = foreignCollectionField.foreignFieldName();
        Class<?> collectionObjectClass = FieldTypeUtils.getCollectionGenericClass(field);

        fieldType.fetchType = foreignCollectionField.fetchType();
        fieldType.collectionType = resolveCollectionType(field);
        fieldType.field = field;
        fieldType.collectionObjectClass = collectionObjectClass;
        fieldType.fieldAccessor = new FieldAccessor(field);

        Field foreignField;

        if (foreignFieldName.isEmpty()) {
            foreignField = FieldTypeUtils.findFieldByType(
                    field.getDeclaringClass(),
                    fieldType.getCollectionObjectClass()
            ).get();
        } else {
            foreignField = FieldTypeUtils.findFieldByName(foreignFieldName, collectionObjectClass);
        }
        fieldType.tableName = DatabaseEntityMetadataUtils.resolveTableName(field.getDeclaringClass());
        fieldType.foreignField = foreignField;
        fieldType.foreignColumnName = FieldTypeUtils.resolveForeignColumnTypeName(foreignField);
        fieldType.foreignTableName = DatabaseEntityMetadataUtils.resolveTableName(foreignField.getDeclaringClass());

        return fieldType;
    }

    /**
     * Resolve collection type by collection class.
     * @param field target collection field
     * @return collection type
     */
    private static CollectionType resolveCollectionType(Field field) {
        Class<?> collectionClass = field.getType();

        if (collectionClass.equals(List.class)) {
            return CollectionType.LIST;
        }

        if (collectionClass.equals(Set.class)) {
            return CollectionType.SET;
        }

        throw new RuntimeException(
                "Unknown collection type for " + field.getDeclaringClass() + " " + field.getName()
        );
    }

    /**
     * Available collection types.
     */
    public enum  CollectionType {

        /**
         * Associate with {@link List}.
         */
        LIST,

        /**
         * Associate with {@link Set}.
         */
        SET,

        /**
         * If collection type not defined.
         */
        UNDEFINED
    }

}
