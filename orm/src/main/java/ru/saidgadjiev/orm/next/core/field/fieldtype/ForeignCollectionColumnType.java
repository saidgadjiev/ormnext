package ru.saidgadjiev.orm.next.core.field.fieldtype;

import ru.saidgadjiev.orm.next.core.dao.visitor.EntityMetadataVisitor;
import ru.saidgadjiev.orm.next.core.field.CollectionType;
import ru.saidgadjiev.orm.next.core.field.FetchType;
import ru.saidgadjiev.orm.next.core.field.FieldAccessor;
import ru.saidgadjiev.orm.next.core.field.persister.DataPersister;
import ru.saidgadjiev.orm.next.core.utils.TableInfoUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Collection;

public class ForeignCollectionColumnType implements IForeignDatabaseColumnType {

    private Field field;

    private Class<?> foreignFieldClass;

    private FetchType fetchType;

    private CollectionType collectionType;

    private FieldAccessor fieldAccessor;

    private ForeignColumnType foreignColumnType;

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
    public String getTableName() {
        return TableInfoUtils.resolveTableName(getOwnerClass());
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
    public ForeignColumnType getForeignColumnType() {
        return foreignColumnType;
    }

    @Override
    public ForeignColumnKey getForeignColumnKey() {
        return new ForeignColumnKey(foreignColumnType.getTableName(), foreignColumnType.getColumnName());
    }

    public void setForeignColumnType(ForeignColumnType foreignColumnType) {
        this.foreignColumnType = foreignColumnType;
    }

    @Override
    public String getForeignTableName() {
        return foreignColumnType.getTableName();
    }

    @Override
    public void accept(EntityMetadataVisitor visitor) {
        visitor.visit(this);
        visitor.finish(this);
    }

}
