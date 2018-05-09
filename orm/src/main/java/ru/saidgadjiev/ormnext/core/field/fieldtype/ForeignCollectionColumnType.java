package ru.saidgadjiev.ormnext.core.field.fieldtype;

import ru.saidgadjiev.ormnext.core.field.*;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.table.internal.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.ormnext.core.utils.DatabaseMetaDataUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ForeignCollectionColumnType implements IForeignDatabaseColumnType {

    private Field field;

    private Field foreignField;

    private Class<?> collectionObjectClass;

    private FetchType fetchType;

    private CollectionType collectionType;

    private FieldAccessor fieldAccessor;

    private String ownerTableName;

    private String foreignColumnName;

    private String foreignTableName;

    private ForeignColumnKey foreignColumnKey;

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
        throw new UnsupportedOperationException();
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

    @Override
    public boolean isForeignCollectionFieldType() {
        return true;
    }

    @Override
    public String getOwnerTableName() {
        return ownerTableName;
    }

    public void setOwnerTableName(String ownerTableName) {
        this.ownerTableName = ownerTableName;
    }

    public void add(Object object, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
                ((Collection<Object>) field.get(object)).add(value);
                field.setAccessible(false);
            } else {
                ((Collection) field.get(object)).add(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addAll(Object object, Collection<?> value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            ((Collection) field.get(object)).addAll(value);
            field.setAccessible(false);
        } else {
            ((Collection) field.get(object)).addAll(value);
        }
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setCollectionObjectClass(Class<?> collectionObjectClass) {
        this.collectionObjectClass = collectionObjectClass;
    }

    @Override
    public Class<?> getCollectionObjectClass() {
        return collectionObjectClass;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
    }

    public void setFieldAccessor(FieldAccessor fieldAccessor) {
        this.fieldAccessor = fieldAccessor;
    }

    @Override
    public ForeignColumnKey getForeignColumnKey() {
        if (foreignColumnKey == null) {
            foreignColumnKey = new ForeignColumnKey(foreignTableName, foreignColumnName);
        }

        return foreignColumnKey;
    }

    @Override
    public String getForeignTableName() {
        return foreignTableName;
    }

    public void setForeignColumnName(String foreignColumnName) {
        this.foreignColumnName = foreignColumnName;
    }

    public void setForeignTableName(String foreignTableName) {
        this.foreignTableName = foreignTableName;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    public Field getForeignField() {
        return foreignField;
    }

    public void setForeignField(Field foreignField) {
        this.foreignField = foreignField;
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        visitor.visit(this);
        visitor.finish(this);
    }

    public static ForeignCollectionColumnType build(Field field) {
        if (!field.isAnnotationPresent(ForeignCollectionField.class)) {
            return null;
        }
        ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
        ForeignCollectionColumnType fieldType = new ForeignCollectionColumnType();
        String foreignFieldName = foreignCollectionField.foreignFieldName();
        Class<?> collectionObjectClass = FieldTypeUtils.getCollectionGenericClass(field);

        fieldType.setFetchType(foreignCollectionField.fetchType());
        fieldType.setCollectionType(resolveCollectionType(field.getType()));
        fieldType.setField(field);
        fieldType.setCollectionObjectClass(collectionObjectClass);
        fieldType.setFieldAccessor(new FieldAccessor(field));

        Field foreignField;

        if (foreignFieldName.isEmpty()) {
            foreignField = FieldTypeUtils.findFieldByType(
                    field.getDeclaringClass(),
                    fieldType.getCollectionObjectClass()
            ).get();
        } else {
            foreignField = FieldTypeUtils.findFieldByName(foreignFieldName, collectionObjectClass);
        }
        fieldType.setForeignField(foreignField);
        fieldType.setForeignColumnName(FieldTypeUtils.resolveForeignColumnTypeName(foreignField));
        fieldType.setForeignTableName(DatabaseMetaDataUtils.resolveTableName(foreignField.getDeclaringClass()));

        return fieldType;
    }

    private static CollectionType resolveCollectionType(Class<?> collectionClass) {
        if (collectionClass.equals(List.class)) {
            return CollectionType.LIST;
        }

        if (collectionClass.equals(Set.class)) {
            return CollectionType.SET;
        }

        return CollectionType.UNDEFINED;
    }

}
