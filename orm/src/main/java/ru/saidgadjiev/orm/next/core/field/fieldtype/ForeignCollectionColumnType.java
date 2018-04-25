package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.field.CollectionType;
import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;
import ru.saidgadjiev.orm.next.core.table.internal.visitor.EntityMetadataVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ForeignCollectionColumnType implements IForeignDatabaseColumnType {

    private Field field;

    private Field foreignField;

    private Class<?> foreignFieldClass;

    private FetchType fetchType;

    private CollectionType collectionType;

    private FieldAccessor fieldAccessor;

    private String ownerTableName;

    private String foreignColumnName;

    private String foreignTableName;

    @Override
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        return fieldAccessor.access(object);
    }

    @Override
    public DataPersister getDataPersister() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void assign(Object object, Object value)  {
        try {
            fieldAccessor.assign(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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

    public void setForeignFieldClass(Class<?> foreignFieldClass) {
        this.foreignFieldClass = foreignFieldClass;
    }

    @Override
    public Class<?> getForeignFieldClass() {
        return foreignFieldClass;
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
        return new ForeignColumnKey(foreignTableName, foreignColumnName);
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

}
