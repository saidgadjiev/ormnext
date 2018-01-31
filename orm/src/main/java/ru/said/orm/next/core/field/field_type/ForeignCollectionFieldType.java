package ru.said.orm.next.core.field.field_type;

import ru.said.orm.next.core.field.DataType;
import ru.said.orm.next.core.field.persisters.DataPersister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ForeignCollectionFieldType implements IDBFieldType {

    private Field field;

    private Class<?> foreignFieldClass;

    private Field foreignField;

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public boolean isNotNull() {
        return false;
    }

    @Override
    public boolean isGenerated() {
        return false;
    }

    @Override
    public String getColumnName() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public DataType getDataType() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Object access(Object object) throws InvocationTargetException, IllegalAccessException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public DataPersister getDataPersister() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void assign(Object object, Object value) throws IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("");
    }

    public Field getField() {
        return field;
    }

    @Override
    public int getLength() {
        throw new UnsupportedOperationException("");
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
}
