package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.dao.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.orm.next.core.field.CollectionType;
import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ForeignCollectionFieldType implements IDatabaseColumnType {

    private Field field;

    private Class<?> foreignFieldClass;

    private Field foreignField;

    private FetchType fetchType;

    private CollectionType collectionType;

    private FieldAccessor fieldAccessor;

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

    public Field getField() {
        return field;
    }

    @Override
    public boolean isForeignCollectionFieldType() {
        return true;
    }

    public void add(Object object, Object value) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
            ((Collection<Object>) field.get(object)).add(value);
            field.setAccessible(false);
        } else {
            ((Collection) field.get(object)).add(value);
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

    public void setForeignField(Field foreignField) {
        this.foreignField = foreignField;
    }

    public Field getForeignField() {
        return foreignField;
    }

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
    public void accept(EntityMetadataVisitor visitor) {

    }
}
